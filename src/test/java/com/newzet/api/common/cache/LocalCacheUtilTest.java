package com.newzet.api.common.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.newzet.api.common.cache.local.LocalCacheUtil;

public class LocalCacheUtilTest {

	private LocalCacheUtil localCacheUtil;

	@BeforeEach
	void setUp() {
		localCacheUtil = new LocalCacheUtil();
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
