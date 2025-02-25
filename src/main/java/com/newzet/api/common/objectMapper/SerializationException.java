package com.newzet.api.common.objectMapper;

public class SerializationException extends RuntimeException {
	public SerializationException(String className, Throwable cause) {
		super(className + " 직렬화 오류", cause);
	}
}
