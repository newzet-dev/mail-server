package com.newzet.api.common.lock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.newzet.api.common.lock.local.LocalLockFactory;

public class LocalLockFactoryTest {

	private final LocalLockFactory localLockFactory = new LocalLockFactory();

	@Test
	public void tryLock_whenLockAcquired_returnLock() {
	    //Given
		String lockKey = "testLock";

	    //When
		Optional<Lock> lock = localLockFactory.tryLock(lockKey, 500, 2000);

	    //Then
		assertTrue(lock.isPresent());
		lock.get().unlock();
	}
	
	@Test
	public void tryLock_whenLockAlreadyHeld_returnEmpty() throws InterruptedException{
		// Given
		String lockKey = "testLock";

		// When
		Optional<Lock> firstLock = localLockFactory.tryLock(lockKey, 500, 5000);
		assertTrue(firstLock.isPresent());

		// 다른 스레드에서 락 시도 → 실패해야 함
		AtomicBoolean secondAcquired = new AtomicBoolean(true);
		Thread thread = new Thread(() -> {
			Optional<Lock> secondLock = localLockFactory.tryLock(lockKey, 500, 5000);
			secondAcquired.set(secondLock.isPresent());
		});
		thread.start();
		thread.join();

		// Then
		assertFalse(secondAcquired.get());

		// Cleanup
		firstLock.get().unlock();
	}
}
