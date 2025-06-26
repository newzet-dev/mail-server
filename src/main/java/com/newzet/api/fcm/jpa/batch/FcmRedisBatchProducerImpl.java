package com.newzet.api.fcm.jpa.batch;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import com.newzet.api.common.objectMapper.OptionalObjectMapper;
import com.newzet.api.fcm.business.batch.FcmBatchProducer;
import com.newzet.api.fcm.domain.FcmNotification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmRedisBatchProducerImpl implements FcmBatchProducer {

	private static final String FCM_STREAM_KEY = "fcm:stream";
	private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
	private final OptionalObjectMapper optionalObjectMapper;

	@Override
	public void addToBatch(FcmNotification fcmNotification) {
		String jsonData = optionalObjectMapper.serialize(fcmNotification);

		Map<String, String> fields = new HashMap<>();
		fields.put("data", jsonData);

		reactiveRedisTemplate.opsForStream()
			.add(FCM_STREAM_KEY, fields)
			.subscribeOn(Schedulers.boundedElastic())
			.doOnSuccess(recordId -> {
				if (log.isDebugEnabled()) {
					log.debug(
						"FCM notification added to batch queue: userId={}, title={}, recordId={}",
						fcmNotification.getUserId(), fcmNotification.getTitle(), recordId);
				}
			})
			.doOnError(error ->
				log.error("Failed to add FCM notification to stream: userId={}, title={}, error={}",
					fcmNotification.getUserId(), fcmNotification.getTitle(), error.getMessage(),
					error)
			)
			.subscribe();
	}
}
