package com.newzet.api.common.lock.redis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.newzet.api.common.lock.LockFactory;
import com.newzet.api.common.lock.exception.RedisLockAcquisitionException;
import com.newzet.api.common.lock.exception.UnlockingFailedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockFactory implements LockFactory {
	private static final String LOCK_PREFIX = "lock:";
	private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

	private final RedissonClient redissonClient;

	@Override
	public Lock tryLock(String lockKey, long waitTime, long leaseTime) {
		RLock lock = redissonClient.getLock(LOCK_PREFIX + lockKey);
		try {
			if (!lock.tryLock(waitTime, leaseTime, TIME_UNIT)) {
				throw new RedisLockAcquisitionException("Redis Lock 획득에 실패하였습니다.");
			}
			return new RedisLock(lock);
		} catch (Exception e) {
			log.error("[RedisLockFactory]: Redis lock 획득 실패, errorMessage: {}", e.getMessage());
			throw new RedisLockAcquisitionException("Redis Lock 획득에 실패하였습니다.");
		}
	}

	@Override
	public void unlock(Lock lock) {
		try{
			lock.unlock();
		} catch(Exception e) {
			log.error("[RedisLockFactory]: Redis lock 해제 실패, error {}", e.getMessage());
			throw new UnlockingFailedException("Redis Lock 해제에 실패하였습니다.");
		}
	}
}

