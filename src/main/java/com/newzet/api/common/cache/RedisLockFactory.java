package com.newzet.api.common.cache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisLockFactory implements LockFactory {
	private static final String LOCK_PREFIX = "lock:";
	private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

	private final RedissonClient redissonClient;

	@Override
	public Optional<Lock> tryLock(String lockKey, long waitTime, long leaseTime) {
		RLock lock = redissonClient.getLock(LOCK_PREFIX + lockKey);
		try {
			boolean acquired = lock.tryLock(waitTime, leaseTime, TIME_UNIT);
			return acquired ? Optional.of(lock) : Optional.empty();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return Optional.empty();
		}
	}

	@Override
	public void unlock(Lock lock) {
		lock.unlock();
	}
}

