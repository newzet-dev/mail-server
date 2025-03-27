package com.newzet.api.common.cache;

import java.util.Optional;

import javax.cache.CacheException;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.newzet.api.common.cache.local.LocalCacheUtil;
import com.newzet.api.common.cache.redis.RedisServerException;
import com.newzet.api.common.cache.redis.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class HybridCacheUtil implements CacheUtil {
	private final RedisUtil redisUtil;
	private final LocalCacheUtil localCacheUtil;

	@Override
	public <T> Optional<T> get(String key, Class<T> classType) {
		try {
			return redisUtil.get(key, classType);
		} catch (RedisServerException e) {
			return localCacheUtil.get(key, classType);
		}
	}

	@Override
	public <T> void set(String key, T object, long ttl) {
		try {
			redisUtil.set(key, object, ttl);
		} catch (CacheException e) {
			localCacheUtil.set(key, object, ttl);
		}
	}
}
