package com.newzet.api.fcm.jpa.batch;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.newzet.api.common.batch.RedisBatchConsumer;
import com.newzet.api.common.batch.config.BatchConfig;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;
import com.newzet.api.fcm.business.batch.FcmBatchConsumer;
import com.newzet.api.fcm.domain.FcmNotification;
import com.newzet.api.fcm.orchestrator.FcmSenderOrchestrator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FcmRedisBatchConsumerImpl extends RedisBatchConsumer<FcmNotification>
	implements FcmBatchConsumer {

	private static final String FCM_STREAM_KEY = "fcm:stream";
	private static final String CONSUMER_GROUP = "fcm-processor-group";
	private static final String CONSUMER_NAME = "fcm-processor";
	private static final int PARALLEL_THREADS = 50;
	private static final int BATCH_TIMEOUT_SECONDS = 8;

	private final FcmSenderOrchestrator fcmSenderOrchestrator;
	private final ExecutorService fcmExecutor;

	public FcmRedisBatchConsumerImpl(RedisTemplate<String, String> redisTemplate,
		ReactiveRedisTemplate<String, String> reactiveRedisTemplate,
		BatchConfig batchConfig,
		OptionalObjectMapper optionalObjectMapper,
		FcmSenderOrchestrator fcmSenderOrchestrator) {
		super(redisTemplate, reactiveRedisTemplate, batchConfig, optionalObjectMapper);
		this.fcmSenderOrchestrator = fcmSenderOrchestrator;

		this.fcmExecutor = Executors.newFixedThreadPool(PARALLEL_THREADS, r -> {
			Thread t = new Thread(r, "fcm-turbo-" + System.nanoTime());
			t.setDaemon(true);
			t.setPriority(Thread.NORM_PRIORITY + 1);
			return t;
		});
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

		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failCount = new AtomicInteger(0);
		AtomicInteger invalidTokenCount = new AtomicInteger(0);

		try {
			log.info("Processing FCM batch: {} notifications", fcmNotifications.size());

			processInParallelTurbo(fcmNotifications, successCount, failCount, invalidTokenCount);

			long duration = System.currentTimeMillis() - startTime;
			logBatchSummary(fcmNotifications.size(), successCount.get(), failCount.get(),
				invalidTokenCount.get(), duration);

		} catch (Exception e) {
			log.error("FCM batch processing failed: {}", e.getMessage(), e);
			failCount.addAndGet(fcmNotifications.size());
		}
	}

	private void processInParallelTurbo(List<FcmNotification> fcmNotifications,
		AtomicInteger successCount, AtomicInteger failCount, AtomicInteger invalidTokenCount) {

		try {
			CompletableFuture<?>[] futures = fcmNotifications.stream()
				.map(notification -> CompletableFuture.runAsync(() -> {
						processSingleNotificationFast(notification, successCount, failCount,
							invalidTokenCount);
					}, fcmExecutor)
					.orTimeout(5, TimeUnit.SECONDS))
				.toArray(CompletableFuture[]::new);

			CompletableFuture.allOf(futures)
				.get(BATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS);

		} catch (Exception e) {
			log.error("FCM processing failed: {}", e.getMessage());
			int totalProcessed = successCount.get() + failCount.get() + invalidTokenCount.get();
			int remainingFails = fcmNotifications.size() - totalProcessed;
			if (remainingFails > 0) {
				failCount.addAndGet(remainingFails);
				log.warn("FCM batch incomplete: {} remaining marked as failed", remainingFails);
			}
		}
	}

	private void processSingleNotificationFast(FcmNotification fcmNotification,
		AtomicInteger successCount, AtomicInteger failCount, AtomicInteger invalidTokenCount) {

		if (!fcmNotification.isValid()) {
			log.warn("Invalid FCM notification: userId={}, token={}",
			fcmNotification.getUserId(), fcmNotification.getToken());
			invalidTokenCount.incrementAndGet();
			return;
		}

		try {
			fcmSenderOrchestrator.send(fcmNotification);
			successCount.incrementAndGet();

		} catch (Exception e) {
			failCount.incrementAndGet();
			log.trace("FCM send failed for userId {}: {}",
				fcmNotification.getUserId(), e.getMessage());
		}
	}

	private void logBatchSummary(int totalNotifications, int successCount, int failCount,
		int invalidTokenCount, long duration) {

		log.info(
			"FCM BATCH: total={}, success={}, failed={}, invalid={}, elapsed={}ms",
			totalNotifications, successCount, failCount, invalidTokenCount, duration);
	}

	@Override
	public void onShutdown() {
		super.onShutdown();
		if (fcmExecutor != null && !fcmExecutor.isShutdown()) {
			log.info("Shutting down FCM executor with {} threads", PARALLEL_THREADS);
			fcmExecutor.shutdown();
			try {
				if (!fcmExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
					fcmExecutor.shutdownNow();
					if (!fcmExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
						log.warn("FCM executor did not terminate gracefully");
					}
				}
			} catch (InterruptedException e) {
				fcmExecutor.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}
}
