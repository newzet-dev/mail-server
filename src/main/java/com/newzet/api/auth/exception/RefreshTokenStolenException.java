package com.newzet.api.auth.exception;

public class RefreshTokenStolenException extends RuntimeException {
	public RefreshTokenStolenException(String message) {
		super(message);
	}
}
