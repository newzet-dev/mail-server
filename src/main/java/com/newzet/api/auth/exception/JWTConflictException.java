package com.newzet.api.auth.exception;

public class JWTConflictException extends RuntimeException {
	public JWTConflictException(String message) {
		super(message);
	}
}
