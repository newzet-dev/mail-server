package com.newzet.api.common.lock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
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
}
