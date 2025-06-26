package com.newzet.api.common.batch;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.ReactiveRedisTemplate;

import com.newzet.api.common.objectMapper.OptionalObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractBatchProducer<T> implements BatchProducer<T> {

	protected final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
	protected final OptionalObjectMapper optionalObjectMapper;

	@Override
	public void addToBatch(T item) {
		String jsonData = optionalObjectMapper.serialize(item);

		Map<String, String> fields = new HashMap<>();
		fields.put("data", jsonData);

		reactiveRedisTemplate.opsForStream()
			.add(getStreamKey(), fields)
			.subscribeOn(Schedulers.boundedElastic())
			.doOnSuccess(recordId -> {
				if (log.isDebugEnabled()) {
					log.debug("{} added to batch queue: {}, recordId: {}",
						getItemTypeName(), getItemIdentifier(item), recordId);
				}
			})
			.doOnError(error ->
				log.error("Failed to add {} to stream: {}, error: {}",
					getItemTypeName(), getItemIdentifier(item), error.getMessage(), error)
			)
			.subscribe();
	}

	protected abstract String getStreamKey();

	protected abstract String getItemTypeName();

	protected abstract String getItemIdentifier(T item);
}
