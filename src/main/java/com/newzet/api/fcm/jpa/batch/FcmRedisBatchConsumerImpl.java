package com.newzet.api.fcm.jpa.batch;

import java.util.List;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.newzet.api.common.batch.RedisBatchConsumer;
import com.newzet.api.common.batch.config.BatchConfig;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;
import com.newzet.api.fcm.business.batch.FcmBatchConsumer;
import com.newzet.api.fcm.business.repository.FcmTokenRepository;
import com.newzet.api.fcm.domain.FcmNotification;
import com.newzet.api.fcm.domain.FcmToken;
import com.newzet.api.fcm.jpa.batch.dto.FcmBatchProcessingResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FcmRedisBatchConsumerImpl extends RedisBatchConsumer<FcmNotification>
	implements FcmBatchConsumer {

	private static final String FCM_STREAM_KEY = "fcm:stream";
	private static final String CONSUMER_GROUP = "fcm-processor-group";
	private static final String CONSUMER_NAME = "fcm-processor";

	private final FcmTokenRepository fcmTokenRepository;
	private final FirebaseMessaging firebaseMessaging;

	public FcmRedisBatchConsumerImpl(RedisTemplate<String, String> redisTemplate,
		ReactiveRedisTemplate<String, String> reactiveRedisTemplate,
		BatchConfig batchConfig,
		OptionalObjectMapper optionalObjectMapper,
		FcmTokenRepository fcmTokenRepository,
		FirebaseMessaging firebaseMessaging) {
		super(redisTemplate, reactiveRedisTemplate, batchConfig, optionalObjectMapper);
		this.fcmTokenRepository = fcmTokenRepository;
		this.firebaseMessaging = firebaseMessaging;
	}

	@Override
	protected String getStreamKey() {
		return FCM_STREAM_KEY;
	}

	@Override
	protected String getConsumerGroup() {
		return CONSUMER_GROUP;
	}

	@Override
	protected String getConsumerName() {
		return CONSUMER_NAME;
	}

	@Override
	protected String getProcessorTypeName() {
		return "FCM";
	}

	@Override
	protected Class<FcmNotification> getItemClass() {
		return FcmNotification.class;
	}

	@Override
	protected void processBatchItems(List<FcmNotification> fcmNotifications) {
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
				FcmToken fcmToken = fcmTokenRepository.findByUserIdAndValue(
					fcmNotification.getUserId(), fcmNotification.getToken());
				fcmTokenRepository.deleteFcmToken(fcmToken);
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
}
