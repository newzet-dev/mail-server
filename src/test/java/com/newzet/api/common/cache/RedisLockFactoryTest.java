package com.newzet.api.common.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;
import com.newzet.api.config.RedisTestContainerConfig;
import com.newzet.api.config.RedissonConfig;

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
		Optional<Lock> lock = redisLockFactory.tryLock(lockKey, 500, 2000);

		// Then
		assertTrue(lock.isPresent());
		assertTrue(((RLock)lock.get()).isHeldByCurrentThread());

		// Cleanup
		lock.get().unlock();
	}

	@Test
	void tryLock_whenLockAlreadyHeld_returnEmpty() throws InterruptedException {
		// Given
		String lockKey = "testLock";
		Optional<Lock> firstLock = redisLockFactory.tryLock(lockKey, 500, 10000);

		assertTrue(firstLock.isPresent());
		assertTrue(((RLock)firstLock.get()).isLocked());

		// When
		AtomicBoolean secondLockAcquired = new AtomicBoolean(true);
		Thread thread = new Thread(() -> {
			Optional<Lock> secondLock = redisLockFactory.tryLock(lockKey, 500, 2000);
			secondLockAcquired.set(secondLock.isPresent());
		});

		thread.start();
		thread.join();

		// Then
		assertFalse(secondLockAcquired.get());

		// Cleanup
		firstLock.get().unlock();
	}

	@Test
	void unlock_whenUnlockOccurs_returnLock() throws InterruptedException {
		// Given
		String lockKey = "testLock";
		Optional<Lock> firstLock = redisLockFactory.tryLock(lockKey, 500, 10000);

		assertTrue(firstLock.isPresent());
		assertTrue(((RLock)firstLock.get()).isLocked());

		// When
		AtomicBoolean secondLockAcquired = new AtomicBoolean(false);
		Thread thread = new Thread(() -> {
			Optional<Lock> secondLock = redisLockFactory.tryLock(lockKey, 500, 2000);
			secondLockAcquired.set(secondLock.isPresent());
			secondLock.ifPresent(lock -> ((RLock)lock).unlock());
		});

		firstLock.get().unlock();
		thread.start();
		thread.join();

		// Then
		assertTrue(secondLockAcquired.get());
	}
}
