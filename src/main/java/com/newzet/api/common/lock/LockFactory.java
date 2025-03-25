package com.newzet.api.common.lock;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

public interface LockFactory {
	public Optional<Lock> tryLock(String lockKey, long waitTime, long leaseTime);

	public void unlock(Lock lock);
}
