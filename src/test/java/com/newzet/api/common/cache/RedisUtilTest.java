package com.newzet.api.common.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;
import com.newzet.api.config.RedisTestContainerConfig;

@DataRedisTest
@Import({ObjectMapper.class, OptionalObjectMapper.class, RedisUtil.class})
@ExtendWith(RedisTestContainerConfig.class)
class RedisUtilTest {

	@Autowired
	private RedisUtil redisUtil;

	@AfterEach
	void cleanUp() {
		redisUtil.deleteAllKeys();
	}

	@Test
	public void set_whenCachedValueNoExists_returnTrue() {
		//Given
		String key = "testKey";
		String value = "testValue";
		long ttl = 60000L;

		//When, Then
		assertTrue(redisUtil.set(key, value, ttl));
	}

	@Test
	public void set_whenCachedValueExists_returnFalse() {
		//Given
		String key = "testKey";
		String value = "testValue";
		long ttl = 60000L;

		//When
		redisUtil.set(key, value, ttl);

		// Then
		assertFalse(redisUtil.set(key, value, ttl));
	}

	@Test
	public void get_whenCachedValueNoExists_returnOptionalEmpty() {
		//Given
		String key = "testKey";
		String value = "testValue";
		long ttl = 60000L;

		//When
		Optional<String> returnValue = redisUtil.get(key, String.class);

		//Then
		assertEquals(Optional.empty(), returnValue);
	}

	@Test
	public void get_whenCachedValueExist_returnValue() {
		//Given
		String key = "testKey";
		String value = "testValue";
		long ttl = 3000L;

		//When
		redisUtil.set(key, value, ttl);
		Optional<String> returnValue = redisUtil.get(key, String.class);

		//Then
		assertTrue(returnValue.isPresent());
		assertEquals(value, returnValue.get());
	}

	@Test
	public void deleteAllKeys_whenKeysExist_deleteAllKeys() {
		//Given
		String key1 = "testKey1";
		String key2 = "testKey2";
		String value1 = "testValue1";
		String value2 = "testValue2";
		long ttl = 60000L;
		redisUtil.set(key1, value1, ttl);
		redisUtil.set(key2, value2, ttl);

		//When
		redisUtil.deleteAllKeys();

		//Then
		assertEquals(Optional.empty(), redisUtil.get(key1, String.class));
		assertEquals(Optional.empty(), redisUtil.get(key2, String.class));
	}
}
