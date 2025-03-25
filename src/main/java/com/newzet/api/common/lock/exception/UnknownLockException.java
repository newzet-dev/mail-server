package com.newzet.api.common.lock.exception;

public class UnknownLockException extends RuntimeException{
	public UnknownLockException(Class<?> lockType) {
		super("Unknown lock type: " + lockType.getName());
	}
}
