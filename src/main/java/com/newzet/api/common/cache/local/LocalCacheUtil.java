package com.newzet.api.common.cache.local;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.newzet.api.common.cache.CacheUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalCacheUtil implements CacheUtil {

	private final Cache<String, CacheEntry<?>> cache;

	@Override
	public <T> Optional<T> get(String key, Class<T> classType) {
		CacheEntry<?> entry = cache.getIfPresent(key);
		if (entry == null) {
			return Optional.empty();
		}

		if (entry.isExpired()) {
			cache.invalidate(key);
			return Optional.empty();
		}

		try {
			return Optional.of(classType.cast(entry.getValue()));
		} catch (ClassCastException e) {
			log.error("[LocalCacheUtil]: Local Cache에서 값 가져오기 실패, key: {}, error: {}", key,
				e.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public <T> void set(String key, T object, long ttl) {
		cache.put(key, new CacheEntry<>(object, ttl));
	}
}
