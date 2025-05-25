package com.newzet.api.common.lock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newzet.api.common.cache.redis.RedisUtil;
import com.newzet.api.common.lock.exception.RedisLockAcquisitionException;
import com.newzet.api.common.lock.redis.RedisLock;
import com.newzet.api.common.lock.redis.RedisLockFactory;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;
import com.newzet.api.config.RedisTestContainerConfig;
import com.newzet.api.config.cache.RedissonConfig;

@DataRedisTest
@Import({ObjectMapper.class, OptionalObjectMapper.class, RedisUtil.class, RedisLockFactory.class,
	RedissonConfig.class})
@ExtendWith(RedisTestContainerConfig.class)
class RedisLockFactoryTest {

	@Autowired
	private RedisLockFactory redisLockFactory;

	@Test
	void tryLock_whenLockAcquired_returnLock() {
		// Given
		String lockKey = "testLock";

		// When
		Lock lock = redisLockFactory.tryLock(lockKey, 500, 2000);

		// Then
		assertNotNull(lock);
		assertTrue(((RedisLock) lock).isHeldByCurrentThread());

		// Cleanup
		lock.unlock();
	}

	@Test
	void tryLock_whenLockAlreadyHeld_throwRedisLockAcquisitionException() throws InterruptedException {
		// Given
		String lockKey = "testLock";
		Lock firstLock = redisLockFactory.tryLock(lockKey, 500, 10000);

		assertNotNull(firstLock);
		assertTrue(((RedisLock)firstLock).isHeldByCurrentThread());

		// When
		AtomicBoolean lockFailed = new AtomicBoolean(false);
		Thread thread = new Thread(() -> {
			try {
				Lock secondLock = redisLockFactory.tryLock(lockKey, 500, 2000);
				secondLock.unlock();
			} catch (RedisLockAcquisitionException e) {
				lockFailed.set(true);
			}
		});

		thread.start();
		thread.join();

		// Then
		assertTrue(lockFailed.get());

		// Cleanup
		firstLock.unlock();
	}

	@Test
	void unlock_whenUnlockOccurs() throws InterruptedException {
		// Given
		String lockKey = "testLock";
		Lock firstLock = redisLockFactory.tryLock(lockKey, 500, 10000);

		assertNotNull(firstLock);
		assertTrue(((RedisLock)firstLock).isHeldByCurrentThread());

		// When
		AtomicBoolean lockFailed = new AtomicBoolean(false);
		Thread thread = new Thread(() -> {
			try{
				Lock secondLock = redisLockFactory.tryLock(lockKey, 500, 2000);
				((RedisLock) secondLock).unlock();
			} catch	(Exception e) {
				lockFailed.set(true);
			}
		});

		firstLock.unlock();
		thread.start();
		thread.join();

		// Then
		assertFalse(lockFailed.get());
	}
}
