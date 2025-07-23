package com.newzet.api.fcm.jpa.batch;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import com.newzet.api.common.batch.RedisBatchProducer;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;
import com.newzet.api.fcm.business.batch.FcmBatchProducer;
import com.newzet.api.fcm.domain.FcmNotification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FcmRedisBatchProducerImpl extends RedisBatchProducer<FcmNotification>
	implements FcmBatchProducer {

	private static final String FCM_STREAM_KEY = "fcm:stream";

	public FcmRedisBatchProducerImpl(ReactiveRedisTemplate<String, String> reactiveRedisTemplate,
		OptionalObjectMapper optionalObjectMapper) {
		super(reactiveRedisTemplate, optionalObjectMapper);
	}

	@Override
	protected String getStreamKey() {
		return FCM_STREAM_KEY;
	}

	@Override
	protected String getItemTypeName() {
		return "FCM notification";
	}

	@Override
	protected String getItemIdentifier(FcmNotification fcmNotification) {
		return String.format("userId=%s, title=%s", fcmNotification.getUserId(),
			fcmNotification.getTitle());
	}
}
