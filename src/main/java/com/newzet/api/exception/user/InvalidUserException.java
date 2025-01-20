package com.newzet.api.exception.user;

public class InvalidUserException extends RuntimeException {
	public InvalidUserException(String message) {
		super(message);
	}
}
