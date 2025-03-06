package com.newzet.api.common.cache;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;
import com.newzet.api.config.TestRedisConfig;

@ExtendWith(TestRedisConfig.class)
@DataRedisTest
@Import({ObjectMapper.class, OptionalObjectMapper.class, RedisUtil.class})
class RedisUtilTest {

	@Autowired
	private RedisUtil redisUtil;

	@Test
	public void set_whenCachedValueNoExists_returnTrue() {
		//Given
		String key = "testKey";
		String value = "testValue";
		long ttl = 3000L;

		//When, Then
		assertTrue(redisUtil.set(key, value, ttl));
	}

	@Test
	public void set_whenCachedValueExists_returnFalse() {
		//Given
		String key = "testKey";
		String value = "testValue";
		long ttl = 3000L;

		//When
		redisUtil.set(key, value, ttl);

		// Then
		assertFalse(redisUtil.set(key, value, ttl));
	}
}
