package com.newzet.api.common.cache.local;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.newzet.api.common.cache.CacheUtil;
import com.querydsl.codegen.utils.model.ClassType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LocalCacheUtil implements CacheUtil {

	private final Cache<String, CacheEntry<?>> cache;

	public LocalCacheUtil() {
		this.cache = Caffeine.newBuilder()
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.maximumSize(10_000)
			.build();
	}

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

		try{
			return Optional.of(classType.cast(entry.getValue()));
		}catch(ClassCastException e) {
			log.error("[LocalCacheUtil]: Local Cache에서 값 가져오기 실패, key: {}, error: {}", key,
				e.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public <T> Boolean set(String key, T object, long ttl) {
		CacheEntry<?> previous = cache.getIfPresent(key);
		cache.put(key, new CacheEntry<>(object, ttl));
		return previous != null;
	}

	@Override
	public void deleteAllKeys() {
		cache.invalidateAll();
	}
}
