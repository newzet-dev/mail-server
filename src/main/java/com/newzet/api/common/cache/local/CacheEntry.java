package com.newzet.api.common.cache.local;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class CacheEntry<T> {
	@Getter
	private final T value;
	private final long expireTime;

	public CacheEntry(T value, long ttlMillis) {
		this.value = value;
		this.expireTime = System.currentTimeMillis() + ttlMillis;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() > expireTime;
	}
}
