package com.newzet.api.common.cache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.newzet.api.common.objectMapper.OptionalObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUtil implements CacheUtil {
	private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

	private final RedisTemplate<String, String> redisTemplate;
	private final OptionalObjectMapper objectMapper;

	@Override
	public <T> Optional<T> get(String key, Class<T> classType) {
		return Optional.empty();
	}

	@Override
	public Boolean set(String key, Object object, long ttl) {
		String value = objectMapper.serialize(object);
		return redisTemplate.opsForValue().setIfAbsent(key, value, ttl, TIME_UNIT);
	}
}
