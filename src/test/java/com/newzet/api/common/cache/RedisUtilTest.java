package com.newzet.api.common.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newzet.api.common.cache.redis.RedisUtil;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;
import com.newzet.api.config.RedisTestContainerConfig;

@DataRedisTest
@Import({ObjectMapper.class, OptionalObjectMapper.class, RedisUtil.class})
@ExtendWith(RedisTestContainerConfig.class)
class RedisUtilTest {

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@AfterEach
	void cleanUp() {
		redisTemplate.delete(redisTemplate.keys("*"));
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
}
