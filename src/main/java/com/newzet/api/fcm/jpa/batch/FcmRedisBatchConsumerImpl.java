package com.newzet.api.fcm.jpa.batch;

import java.util.List;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.newzet.api.common.batch.RedisBatchConsumer;
import com.newzet.api.common.batch.config.BatchConfig;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;
import com.newzet.api.fcm.business.batch.FcmBatchConsumer;
import com.newzet.api.fcm.domain.FcmNotification;
import com.newzet.api.fcm.jpa.batch.dto.FcmBatchProcessingResult;
import com.newzet.api.fcm.orchestrator.FcmSenderOrchestrator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FcmRedisBatchConsumerImpl extends RedisBatchConsumer<FcmNotification>
	implements FcmBatchConsumer {

	private static final String FCM_STREAM_KEY = "fcm:stream";
	private static final String CONSUMER_GROUP = "fcm-processor-group";
	private static final String CONSUMER_NAME = "fcm-processor";

	private final FcmSenderOrchestrator fcmSenderOrchestrator;

	public FcmRedisBatchConsumerImpl(RedisTemplate<String, String> redisTemplate,
		ReactiveRedisTemplate<String, String> reactiveRedisTemplate,
		BatchConfig batchConfig,
		OptionalObjectMapper optionalObjectMapper,
		FcmSenderOrchestrator fcmSenderOrchestrator) {
		super(redisTemplate, reactiveRedisTemplate, batchConfig, optionalObjectMapper);
		this.fcmSenderOrchestrator = fcmSenderOrchestrator;
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
			fcmSenderOrchestrator.send(fcmNotification);
			result.incrementSuccessCount();
		} catch (Exception e) {
			result.incrementFailCount();
		}
	}

	private void logBatchSummary(int totalNotifications, FcmBatchProcessingResult result,
		long duration) {
		log.info(
			"FCM BATCH SUMMARY: total={}, success={}, failed={}, invalidToken={}, elapsed={}ms",
			totalNotifications, result.getSuccessCount(), result.getFailCount(),
			result.getInvalidTokenCount(), duration);
	}
}
