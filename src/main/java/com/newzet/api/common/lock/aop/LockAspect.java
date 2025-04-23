package com.newzet.api.common.lock.aop;

import java.util.concurrent.locks.Lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.newzet.api.common.lock.LockFactory;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {

	private final LockFactory lockFactory;

	@Around("@annotation(withLock)")
	public Object handleLock(ProceedingJoinPoint joinPoint, WithLock withLock) throws Throwable {
		String prefix = withLock.prefix();
		String domain = withLock.domain();
		long waitTime = withLock.waitTime();
		long leaseTime = withLock.leaseTime();

		Lock lock = null;
		try {
			lock = lockFactory.tryLock(prefix + ":" + domain, waitTime, leaseTime);
			return joinPoint.proceed();
		} finally {
			lockFactory.unlock(lock);
		}
	}
}

