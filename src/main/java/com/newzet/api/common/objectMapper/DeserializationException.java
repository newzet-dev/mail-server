package com.newzet.api.common.objectMapper;

public class DeserializationException extends RuntimeException {
	public DeserializationException(String className, Throwable cause) {
		super(className + " 역직렬화 오류", cause);
	}
}
