package com.newzet.api.common.lock;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.newzet.api.common.lock.exception.RedisLockAcquisitionException;
import com.newzet.api.common.lock.local.LocalLock;
import com.newzet.api.common.lock.local.LocalLockFactory;
import com.newzet.api.common.lock.redis.RedisLock;
import com.newzet.api.common.lock.redis.RedisLockFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class HybridLockFactory implements LockFactory{
	private final RedisLockFactory redisLockFactory;
	private final LocalLockFactory localLockFactory;

	@Override
	public Optional<Lock> tryLock(String lockKey, long waitTime, long leaseTime) {
		try {
			return redisLockFactory.tryLock(lockKey, waitTime, leaseTime)
				.or(() -> localLockFactory.tryLock(lockKey, waitTime, leaseTime));
		} catch (RedisLockAcquisitionException e) {
			return localLockFactory.tryLock(lockKey, waitTime, leaseTime);
		}
	}

	@Override
	public void unlock(Lock lock) {
		if (lock instanceof RedisLock) {
			redisLockFactory.unlock(lock);
		} else if (lock instanceof LocalLock) {
			localLockFactory.unlock(lock);
		} else {
			log.error("[HybridLockFactory]: 알 수 없는 락 타입 {}", lock.getClass().getName());
		}
	}
}
