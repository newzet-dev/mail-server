package com.newzet.api.common.exception;

public abstract class NewzetException extends RuntimeException {
	private final String message;
	private final ResponseCode responseCode;

	protected NewzetException(String message, ResponseCode responseCode) {
		this.message = message;
		this.responseCode = responseCode;
	}

	public String getMessage() {
		return message;
	}

	public ResponseCode getResponseCode() {
		return responseCode;
	}
}
