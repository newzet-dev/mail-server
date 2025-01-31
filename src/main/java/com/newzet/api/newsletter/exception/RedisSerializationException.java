package com.newzet.api.newsletter.exception;

public class RedisSerializationException extends RuntimeException {
	public RedisSerializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
