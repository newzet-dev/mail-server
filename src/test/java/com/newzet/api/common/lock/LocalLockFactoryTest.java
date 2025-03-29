package com.newzet.api.common.lock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.Test;

import com.newzet.api.common.lock.exception.LocalLockAcquisitionException;
import com.newzet.api.common.lock.local.LocalLockFactory;

public class LocalLockFactoryTest {

	private final LocalLockFactory localLockFactory = new LocalLockFactory();

	@Test
	public void tryLock_whenLockAcquired_returnLock() {
	    //Given
		String lockKey = "testLock";

	    //When
		Lock lock = localLockFactory.tryLock(lockKey, 500, 2000);

	    //Then
		assertNotNull(lock);
		lock.unlock();
	}
	
	@Test
	public void tryLock_whenLockAlreadyHeld_throwLocalLockAcquisitionException() throws InterruptedException{
		// Given
		String lockKey = "testLock";

		// When
		Lock firstLock = localLockFactory.tryLock(lockKey, 500, 5000);
		assertNotNull(firstLock);

		AtomicBoolean lockFailed = new AtomicBoolean(false);
		Thread thread = new Thread(() -> {
			try{
				Lock secondLock = localLockFactory.tryLock(lockKey, 500, 5000);
				secondLock.unlock();
			} catch (LocalLockAcquisitionException e){
				lockFailed.set(true);
			}
		});
		thread.start();
		thread.join();

		// Then
		assertTrue(lockFailed.get());
		firstLock.unlock();
	}

	@Test
	public void unlock_whenUnlockOccurs() throws InterruptedException{
		// Given
		String lockKey = "testLock";

		// When
		Lock firstLock = localLockFactory.tryLock(lockKey, 500, 5000);
		assertNotNull(firstLock);
		firstLock.unlock();

		// Then
		Lock secondLock = localLockFactory.tryLock(lockKey, 500, 1000);
		assertNotNull(secondLock);

		secondLock.unlock();
	}
}
