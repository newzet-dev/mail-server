package com.newzet.api.common.lock.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithLock {
	String prefix();
	String key();
	long waitTime() default 1000*5L;
	long leaseTime() default 1000*3L;
}
