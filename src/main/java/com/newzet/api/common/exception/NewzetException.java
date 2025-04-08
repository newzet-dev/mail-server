package com.newzet.api.common.exception;

public abstract class NewzetException extends RuntimeException {
	private final String message;

	protected NewzetException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
