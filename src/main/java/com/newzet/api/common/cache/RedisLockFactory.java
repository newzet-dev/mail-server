package com.newzet.api.common.cache;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisLockFactory implements LockFactory {

	@Override
	public Optional<Lock> tryLock(String lockKey, long waitTime, long leaseTime) {
		return null;
	}

	@Override
	public void unlock(Lock lock) {
		return null;
	}
}
