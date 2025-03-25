package com.newzet.api.common.lock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.common.lock.local.LocalLock;
import com.newzet.api.common.lock.local.LocalLockFactory;
import com.newzet.api.common.lock.redis.RedisLock;
import com.newzet.api.common.lock.redis.RedisLockFactory;

@ExtendWith(MockitoExtension.class)
class HybridLockFactoryTest {

	@Mock
	private RedisLockFactory redisLockFactory;

	@Mock
	private LocalLockFactory localLockFactory;

	@InjectMocks
	private HybridLockFactory hybridLockFactory;

	@Mock
	private RedisLock redisLock;

	@Mock
	private LocalLock localLock;

	@Test
	public void tryLock_whenRedisLockFactorySuccess_returnRedisLock() {
		// Given
		String key = "testLock";
		when(redisLockFactory.tryLock(eq(key), anyLong(), anyLong()))
			.thenReturn(Optional.of(redisLock));

		// When
		Optional<Lock> lock = hybridLockFactory.tryLock(key, 500, 1000);

		// Then
		assertTrue(lock.isPresent());
		assertEquals(redisLock, lock.get());
	}
}
