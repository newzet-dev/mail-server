package com.newzet.api.common.lock.aop;

import java.util.concurrent.locks.Lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
		String key = resolveKey(joinPoint, withLock);
		long waitTime = withLock.waitTime();
		long leaseTime = withLock.leaseTime();

		Lock lock = lockFactory.tryLock(prefix + ":" + key, waitTime, leaseTime);

		// 현재 쓰레드에 트랜잭션이 열려 있다면 해제 예약
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCompletion(int status) {
					lockFactory.unlock(lock);
				}
			});
		} else {
			// 트랜잭션 없으면 즉시 해제
			lockFactory.unlock(lock);
		}

		return joinPoint.proceed();
	}

	// 명시된 파라미터에 해당하는 값을 런타임 시점에 찾아와, 바인딩해줌
	private String resolveKey(ProceedingJoinPoint joinPoint, WithLock withLock) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String[] paramNames = signature.getParameterNames();
		Object[] args = joinPoint.getArgs();

		// SpEL context에 파라미터 이름/값 등록
		EvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < paramNames.length; i++) {
			context.setVariable(paramNames[i], args[i]);
		}

		// key SpEL 파싱
		ExpressionParser parser = new SpelExpressionParser();
		String parsedKey = parser.parseExpression(withLock.key()).getValue(context, String.class);

		return withLock.prefix() + parsedKey;
	}


}

