package com.newzet.api.common.lock.local;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import com.newzet.api.common.lock.LockFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LocalLockFactory implements LockFactory {

	private final ConcurrentHashMap<String, Lock> locks = new ConcurrentHashMap<>();

	@Override
	public Optional<Lock> tryLock(String lockKey, long waitTime, long leaseTime) {
		Lock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantLock());
		try {
			boolean acquired = lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
			return acquired ? Optional.of(new LocalLock(lockKey, lock)) : Optional.empty();
		} catch (Exception e) {
			log.error("[LocalLockFactory]: Redis lock 획득 실패, errorMessage: {}", e.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public void unlock(Lock lock) {
		try {
			lock.unlock();
			locks.remove(((LocalLock)lock).getLockKey());
		} catch (Exception e) {
			log.error("[LocalLockFactory]: Local lock 해제 실패, error {}", e.getMessage());
		}
	}
}
