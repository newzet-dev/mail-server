package com.newzet.api.common.logging;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

	@Pointcut("execution(* com.newzet..api..business..*Service.*(..))")
	public void servicePointcut() {
	}

	// @Pointcut("execution(* com.newzet..api..domain..*.*(..))")
	// public void domainPointcut() {
	// }

	@Pointcut("execution(* com.newzet..api..repository..*RepositoryImpl.*(..))")
	public void repositoryPointcut() {
	}

	@AfterThrowing(pointcut = "servicePointcut() || repositoryPointcut()", throwing = "e")
	public void logException(JoinPoint joinPoint, Throwable e) {
		String method = joinPoint.getSignature().toShortString();
		String args = Arrays.toString(joinPoint.getArgs());

		log.error("[예외 발생] 메서드: {} / 파라미터: {}\n[메시지] {}\n[스택-트레이스]",
			method,
			args,
			e.getMessage(),
			e
		);
	}
}
