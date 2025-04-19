package com.newzet.api.common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SuccessResponse<T> {
	private final Integer code;
	private final String message;
	private final T result;

	public static <T> SuccessResponse<T> create(ResponseCode code, String message, T responseDTO) {
		return new SuccessResponse<>(code.getCode(), message, responseDTO);
	}
}
