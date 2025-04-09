package com.newzet.api.auth.exception;

public class JWTBadRequestException extends RuntimeException {
	public JWTBadRequestException(String message) {
		super(message);
	}
}
