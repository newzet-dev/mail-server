package com.newzet.api.fcm.jpa.batch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.newzet.api.common.batch.config.BatchConfig;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;
import com.newzet.api.fcm.business.batch.FcmBatchConsumer;
import com.newzet.api.fcm.domain.FcmNotification;
import com.newzet.api.fcm.jpa.batch.dto.FcmBatchProcessingResult;
import com.newzet.api.fcm.orchestrator.FcmTokenOrchestrator;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmRedisBatchConsumerImpl implements FcmBatchConsumer {

	private static final String FCM_STREAM_KEY = "fcm:stream";
	private static final String CONSUMER_GROUP = "fcm-processor-group";
	private static final String CONSUMER_NAME = "fcm-processor";

	private final RedisTemplate<String, String> redisTemplate;
	private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
	private final FcmTokenOrchestrator fcmTokenOrchestrator;
	private final BatchConfig batchConfig;
	private final OptionalObjectMapper optionalObjectMapper;
	private final FirebaseMessaging firebaseMessaging;
	private final AtomicBoolean isProcessing = new AtomicBoolean(false);
	private ExecutorService executorService;
	private ExecutorService ackExecutorService;

	@PostConstruct
	public void init() {
		try {
			Boolean exists = redisTemplate.hasKey(FCM_STREAM_KEY);
			if (Boolean.FALSE.equals(exists)) {
				Map<String, String> dummy = new HashMap<>();
				dummy.put("init", "init");
				redisTemplate.opsForStream().add(FCM_STREAM_KEY, dummy);
				log.info("Created new Redis Stream: {}", FCM_STREAM_KEY);
			}

			try {
				redisTemplate.opsForStream().createGroup(FCM_STREAM_KEY, CONSUMER_GROUP);
				log.info("Created new consumer group: {} for stream: {}", CONSUMER_GROUP,
					FCM_STREAM_KEY);
			} catch (Exception e) {
				log.debug("Consumer group may already exist: {}", e.getMessage());
			}
		} catch (Exception e) {
			log.error("Failed to initialize Redis Stream: {}", e.getMessage(), e);
		}
	}

	@Override
	public Map<String, Object> getBatchStatus() {
		Map<String, Object> status = new HashMap<>();
		status.put("isProcessing", isProcessing.get());
		status.put("batchSize", batchConfig.getBatchSize());

		Long pendingCount = redisTemplate.opsForStream().size(FCM_STREAM_KEY);
		pendingCount = (pendingCount != null && pendingCount > 0) ? pendingCount - 1 : 0;
		status.put("pendingMessages", pendingCount != null ? pendingCount : 0);

		return status;
	}

	@Override
	public void startProcessing() {
		if (isProcessing.compareAndSet(false, true)) {
			executorService = Executors.newSingleThreadExecutor();
			ackExecutorService = Executors.newSingleThreadExecutor();
			executorService.submit(this::processBatchesAsync);
			log.info("FCM batch processor started with configuration: size={}, timeout={}s",
				batchConfig.getBatchSize(), batchConfig.getTimeoutSeconds());
		}
	}

	@Override
	public void stopProcessing() {
		if (isProcessing.compareAndSet(true, false)) {
			if (executorService != null) {
				executorService.shutdownNow();
				log.info("FCM batch processor stopped");
			}
			if (ackExecutorService != null) {
				ackExecutorService.shutdownNow();
			}
		}
	}

	private void processBatchesAsync() {
		log.info("FCM batch processing thread initialized");

		try {
			StreamReceiver.StreamReceiverOptions<String, MapRecord<String, String, String>> options =
				StreamReceiver.StreamReceiverOptions.builder()
					.pollTimeout(Duration.ofSeconds(1))
					.build();

			StreamReceiver<String, MapRecord<String, String, String>> receiver =
				StreamReceiver.create(reactiveRedisTemplate.getConnectionFactory(), options);

			receiver.receive(
					Consumer.from(CONSUMER_GROUP, CONSUMER_NAME),
					StreamOffset.create(FCM_STREAM_KEY, ReadOffset.lastConsumed())
				)
				.bufferTimeout(batchConfig.getBatchSize(),
					Duration.ofSeconds(batchConfig.getTimeoutSeconds()))
				.doOnNext(records -> {
					if (!records.isEmpty()) {
						log.info("Processing FCM batch: count={}", records.size());
						processBatchWithAck(records);
					}
				})
				.doOnError(
					error -> log.error("FCM stream processing error: {}", error.getMessage(),
						error))
				.subscribe();

			log.info("Subscribed to FCM Redis Stream with consumer group: {}, consumer: {}",
				CONSUMER_GROUP, CONSUMER_NAME);
		} catch (Exception e) {
			log.error("Failed to initialize FCM batch processor: {}", e.getMessage(), e);
			isProcessing.set(false);
		}
	}

	private void processBatchWithAck(List<MapRecord<String, String, String>> records) {
		List<FcmNotification> fcmNotifications = new ArrayList<>();

		for (MapRecord<String, String, String> record : records) {
			String data = record.getValue().get("data");
			Optional<FcmNotification> fcmNotification = optionalObjectMapper.deserialize(data,
				FcmNotification.class);
			fcmNotification.ifPresent(fcmNotifications::add);
		}

		processBatchItems(fcmNotifications);

		CompletableFuture.runAsync(() -> {
			List<String> ackedMessageIds = new ArrayList<>();

			for (MapRecord<String, String, String> record : records) {
				String messageId = record.getId().getValue();
				try {
					redisTemplate.opsForStream()
						.acknowledge(FCM_STREAM_KEY, CONSUMER_GROUP, messageId);
					ackedMessageIds.add(messageId);
				} catch (Exception e) {
					log.warn("Ack failed for FCM message {}, skipping for now: {}", messageId,
						e.getMessage());
				}
			}

			if (!ackedMessageIds.isEmpty()) {
				try {
					redisTemplate.opsForStream()
						.delete(FCM_STREAM_KEY, ackedMessageIds.toArray(new String[0]));
				} catch (Exception e) {
					log.error("Failed to delete acked FCM messages: {}", e.getMessage(), e);
				}
			}
		}, ackExecutorService);
	}

	private void processBatchItems(List<FcmNotification> fcmNotifications) {
		long startTime = System.currentTimeMillis();
		FcmBatchProcessingResult result = new FcmBatchProcessingResult();

		for (FcmNotification fcmNotification : fcmNotifications) {
			processSingleNotification(fcmNotification, result);
		}

		long duration = System.currentTimeMillis() - startTime;
		logBatchSummary(fcmNotifications.size(), result, duration);
	}

	private void processSingleNotification(FcmNotification fcmNotification,
		FcmBatchProcessingResult result) {
		if (!fcmNotification.isValid()) {
			result.incrementInvalidTokenCount();
			log.warn("Invalid FCM notification: userId={}, token={}",
				fcmNotification.getUserId(), fcmNotification.getToken());
			return;
		}

		try {
			Message message = buildFcmMessage(fcmNotification);
			String response = firebaseMessaging.send(message);
			result.incrementSuccessCount();

			if (log.isDebugEnabled()) {
				log.debug("FCM sent successfully: userId={}, response={}",
					fcmNotification.getUserId(), response);
			}
		} catch (Exception e) {
			handleSendFailure(fcmNotification, result, e);
		}
	}

	private Message buildFcmMessage(FcmNotification fcmNotification) {
		return Message.builder()
			.setToken(fcmNotification.getToken())
			.setNotification(Notification.builder()
				.setTitle(fcmNotification.getTitle())
				.setBody(fcmNotification.getBody())
				.build())
			.putData("data", fcmNotification.getData() != null ? fcmNotification.getData() : "")
			.build();
	}

	private void handleSendFailure(FcmNotification fcmNotification, FcmBatchProcessingResult result,
		Exception e) {
		result.incrementFailCount();
		log.error("Failed to send FCM notification: userId={}, token={}, error={}",
			fcmNotification.getUserId(), fcmNotification.getToken(), e.getMessage(), e);

		if (isInvalidTokenError(e)) {
			try {
				fcmTokenOrchestrator.deleteFcmToken(fcmNotification.getUserId(),
					fcmNotification.getToken());
				log.info("Deleted invalid FCM token: userId={}, token={}",
					fcmNotification.getUserId(), fcmNotification.getToken());
			} catch (Exception deleteError) {
				log.error("Failed to delete invalid FCM token: {}", deleteError.getMessage(),
					deleteError);
			}
		}
	}

	private boolean isInvalidTokenError(Exception e) {
		String errorMessage = e.getMessage().toLowerCase();
		return errorMessage.contains("invalid registration token") ||
			errorMessage.contains("registration token not registered") ||
			errorMessage.contains("invalid argument") ||
			errorMessage.contains("requested entity was not found");
	}

	private void logBatchSummary(int totalNotifications, FcmBatchProcessingResult result,
		long duration) {
		log.info(
			"FCM BATCH SUMMARY: total={}, success={}, failed={}, invalidToken={}, elapsed={}ms",
			totalNotifications, result.getSuccessCount(), result.getFailCount(),
			result.getInvalidTokenCount(), duration);
	}

	@PreDestroy
	public void onShutdown() {
		stopProcessing();
	}
}
