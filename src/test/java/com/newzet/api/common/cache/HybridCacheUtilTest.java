package com.newzet.api.common.cache;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.common.cache.local.LocalCacheUtil;
import com.newzet.api.common.cache.redis.RedisServerException;
import com.newzet.api.common.cache.redis.RedisUtil;

@ExtendWith(MockitoExtension.class)
public class HybridCacheUtilTest {
	@Mock
	RedisUtil redisUtil;

	@Mock
	LocalCacheUtil localCacheUtil;

	@InjectMocks
	HybridCacheUtil hybridCacheUtil;

	@Test
	void get_whenRedisLive_returnRedisValue() {
		//Given
		String key = "testKey";
		String value = "testValue";
		when(redisUtil.get(key, String.class)).thenReturn(Optional.of(value));

		//When
		Optional<String> result = hybridCacheUtil.get(key, String.class);

		//Then
		assertTrue(result.isPresent());
		assertEquals(value, result.get());
		verify(redisUtil, times(1)).get(key, String.class);
		verify(localCacheUtil, never()).get(any(), any());
	}

	@Test
	void get_whenRedisDied_returnLocalCacheValue() {
		//Given
		String key = "testKey";
		String value = "testValue";
		doThrow(new RedisServerException()).when(redisUtil).get(key, String.class);
		when(localCacheUtil.get(key, String.class)).thenReturn(Optional.of(value));

		//When
		Optional<String> result = hybridCacheUtil.get(key, String.class);

		//Then
		assertTrue(result.isPresent());
		assertEquals(value, result.get());
		verify(redisUtil, times(1)).get(key, String.class);
		verify(localCacheUtil, times(1)).get(any(), any());
	}

	@Test
	void set_whenRedisLive_callRedisUtilSet() {
		//Given
		String key = "testKey";
		String value = "testValue";

		//When
		hybridCacheUtil.set(key, value, 1000L);

		//Then
		verify(redisUtil).set(key, value, 1000L);
		verify(localCacheUtil, never()).set(any(), any(), anyLong());
	}

	@Test
	void set_whenRedisDied_callLocalCacheUtilSet() {//Given
		String key = "testKey";
		String value = "testValue";
		doThrow(new RedisServerException()).when(redisUtil).set(key, value, 1000L);

		//When
		hybridCacheUtil.set(key, value, 1000L);

		//Then
		verify(redisUtil, times(1)).set(key, value, 1000L);
		verify(localCacheUtil, times(1)).set(key, value, 1000L);
	}
}
