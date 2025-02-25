package com.newzet.api.common.cache;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import com.newzet.api.config.TestRedisConfig;

// perInstance로 설정하여 docker 컨테이너 실행
@ExtendWith(TestRedisConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RedisLockFactoryIntegrationTest {

	private RedissonClient redissonClient;
	private RedisLockFactory redisLockFactory;

	@BeforeAll
	void setUp() {
		String redisHost = System.getProperty("spring.data.redis.host");
		String redisPort = System.getProperty("spring.data.redis.port");
		String redisUrl = "redis://" + redisHost + ":" + redisPort;

		Config config = new Config();
		config.useSingleServer().setAddress(redisUrl);
		redissonClient = Redisson.create(config);

		redisLockFactory = new RedisLockFactory(redissonClient);
	}

	@AfterAll
	void tearDown() {
		redissonClient.shutdown();
	}

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
