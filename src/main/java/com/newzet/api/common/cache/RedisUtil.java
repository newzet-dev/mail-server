package com.newzet.api.common.cache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.newzet.api.common.cache.exception.RedisServerException;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil implements CacheUtil {
	private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

	private final RedisTemplate<String, String> redisTemplate;
	private final OptionalObjectMapper objectMapper;

	@Override
	public <T> Optional<T> get(String key, Class<T> classType) {
		try{
			String cachedValue = redisTemplate.opsForValue().get(key);
			return objectMapper.deserialize(cachedValue, classType);
		}catch(Exception e) {
			log.error("[RedisUtil]: Redis 에서 key 가져오기 실패, key: {}, error: {}", key, e.getMessage());
			throw new RedisServerException();
		}

	}

	@Override
	public Boolean set(String key, Object object, long ttl) {
		try{
			String value = objectMapper.serialize(object);
			return redisTemplate.opsForValue().setIfAbsent(key, value, ttl, TIME_UNIT);
		} catch (Exception e) {
			log.error("[RedisUtil]: Redis 값 저장 실패, key: {}, error: {}", key, e.getMessage());
			throw new RedisServerException();
		}
	}

	@Override
	public void deleteAllKeys() {
		try{
			redisTemplate.delete(redisTemplate.keys("*"));
		} catch(Exception e) {
			log.error("[RedisUtil]: Redis에서 key 모두 삭제 실패, error: {}", e.getMessage());
			throw new RedisServerException();
		}

	}
}
