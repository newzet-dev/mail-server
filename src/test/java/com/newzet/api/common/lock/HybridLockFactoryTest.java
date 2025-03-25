package com.newzet.api.common.lock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.common.lock.exception.RedisLockAcquisitionException;
import com.newzet.api.common.lock.exception.UnknownLockException;
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
	
	@Test
	public void tryLock_whenRedisLockFail_returnLocalLock() {
		// Given
		String key = "testLock";
		when(redisLockFactory.tryLock(eq(key), anyLong(), anyLong()))
			.thenReturn(Optional.empty());
		when(localLockFactory.tryLock(eq(key), anyLong(), anyLong()))
			.thenReturn(Optional.of(localLock));

		// When
		Optional<Lock> lock = hybridLockFactory.tryLock(key, 500, 1000);

		// Then
		assertTrue(lock.isPresent());
		assertEquals(localLock, lock.get());
	}
	
	@Test
	public void tryLock_whenThrowRedisLockAcquisitionException_returnLocalLock() {
		// Given
		String key = "testLock";
		when(redisLockFactory.tryLock(eq(key), anyLong(), anyLong()))
			.thenThrow(new RedisLockAcquisitionException());
		when(localLockFactory.tryLock(eq(key), anyLong(), anyLong()))
			.thenReturn(Optional.of(localLock));

		// When
		Optional<Lock> lock = hybridLockFactory.tryLock(key, 500, 1000);

		// Then
		assertTrue(lock.isPresent());
		assertEquals(localLock, lock.get());
	}

	@Test
	public void unlock_whenRedisLockUnlock() {
		// When
		hybridLockFactory.unlock(redisLock);

		// Then
		verify(redisLockFactory).unlock(redisLock);
	}

	@Test
	void unlock_whenLocalLockUnlock() {
		// When
		hybridLockFactory.unlock(localLock);

		// Then
		verify(localLockFactory).unlock(localLock);
	}

	@Test
	void unlock_whenUnknownLock_throwUnknownLockException() {
		// Given
		Lock unknowLock = new Lock() {
			@Override public void lock() {}
			@Override public void lockInterruptibly() {}
			@Override public boolean tryLock() { return false; }
			@Override public boolean tryLock(long time, TimeUnit unit) { return false; }
			@Override public void unlock() {}
			@Override public Condition newCondition() { return null; }
		};

		// When , Then
		assertThrows(UnknownLockException.class, () -> hybridLockFactory.unlock(unknowLock));
	}
}
