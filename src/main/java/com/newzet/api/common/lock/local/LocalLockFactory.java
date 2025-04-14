package com.newzet.api.common.lock.local;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import com.newzet.api.common.lock.LockFactory;
import com.newzet.api.common.lock.exception.LocalLockAcquisitionException;
import com.newzet.api.common.lock.exception.UnlockingFailedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LocalLockFactory implements LockFactory {

	private final ConcurrentHashMap<String, Lock> locks = new ConcurrentHashMap<>();

	@Override
	public Lock tryLock(String lockKey, long waitTime, long leaseTime) {
		Lock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantLock());
		try {
			if (!lock.tryLock(waitTime, TimeUnit.MILLISECONDS)) {
				throw new LocalLockAcquisitionException("Local Lock 획득에 실패하였습니다.");
			}
			return new LocalLock(lockKey, lock);
		} catch (Exception e) {
			log.error("[LocalLockFactory]: Local lock 획득 실패, errorMessage: {}", e.getMessage());
			throw new LocalLockAcquisitionException("Local Lock 획득에 실패하였습니다.");
		}
	}

	@Override
	public void unlock(Lock lock) {
		try {
			lock.unlock();
			locks.remove(((LocalLock)lock).getLockKey());
		} catch (Exception e) {
			log.error("[LocalLockFactory]: Local lock 해제 실패, error {}", e.getMessage());
			throw new UnlockingFailedException("Local Lock 해제에 실패하였습니다.");
		}
	}
}
