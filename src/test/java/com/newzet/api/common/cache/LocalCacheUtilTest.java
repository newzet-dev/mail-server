package com.newzet.api.common.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.newzet.api.common.cache.local.CacheEntry;
import com.newzet.api.common.cache.local.LocalCacheUtil;


public class LocalCacheUtilTest {

	private LocalCacheUtil localCacheUtil;

	@BeforeEach
	void setUp() {
		Cache<String, CacheEntry<?>> cache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
		localCacheUtil = new LocalCacheUtil(cache);
	}

	@Test
	public void get_whenValueIsValid_returnOptionalValue() {
		//Given
		String key = "testKey";
		String value = "testValue";
		long ttl = 1000L;

		localCacheUtil.set(key, value, ttl);
		assertTrue(localCacheUtil.get(key, String.class).isPresent());

		//When
		Optional<String> cachedValue = localCacheUtil.get(key, String.class);

		//Then
		assertTrue(cachedValue.isPresent());
		assertEquals(value, cachedValue.get());
	}

	@Test
	public void get_whenValueNotExist_returnOptionalEmpty() {
		//Given
		String key = "testKey";

		//When
		Optional<String> cachedValue = localCacheUtil.get(key, String.class);

		//Then
		assertFalse(cachedValue.isPresent());
	}

	@Test
	public void get_whenValueIsExpired_returnOptionalEmpty() throws InterruptedException {
		//Given
		String key = "testKey";
		String value = "testValue";
		long ttl = 1000L;

		localCacheUtil.set(key, value, ttl);
		assertTrue(localCacheUtil.get(key, String.class).isPresent());

		//When
		Thread.sleep(1100);
		Optional<String> cachedValue = localCacheUtil.get(key, String.class);

		//Then
		assertFalse(localCacheUtil.get(key, String.class).isPresent());
	}
}
