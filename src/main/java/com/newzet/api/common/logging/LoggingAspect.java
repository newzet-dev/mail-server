package com.newzet.api.common.logging;

// @Aspect
// @Component
// @Slf4j
public class LoggingAspect {

	// @Pointcut("execution(* com.newzet..api..business..*Service.*(..))")
	// public void servicePointcut() {
	// }
	//
	// // @Pointcut("execution(* com.newzet..api..domain..*.*(..))")
	// // public void domainPointcut() {
	// // }
	//
	// @Pointcut("execution(* com.newzet..api..repository..*RepositoryImpl.*(..))")
	// public void repositoryPointcut() {
	// }
	//
	// @Pointcut("execution(* com.newzet..api.common.lock..*LockFactory.*(..))")
	// public void LockFactoryPointcut() {
	// }
	//
	// @AfterThrowing(pointcut = "servicePointcut() || repositoryPointcut() || LockFactoryPointcut()", throwing = "e")
	// public void logException(JoinPoint joinPoint, Throwable e) {
	// 	String method = joinPoint.getSignature().toShortString();
	// 	String args = Arrays.toString(joinPoint.getArgs());
	//
	// 	log.error("[예외 발생] 메서드: {} / 파라미터: {}\n[메시지] {}\n[스택-트레이스]",
	// 		method,
	// 		args,
	// 		e.getMessage(),
	// 		e
	// 	);
	// }
}
