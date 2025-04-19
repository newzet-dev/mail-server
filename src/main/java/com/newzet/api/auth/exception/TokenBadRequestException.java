package com.newzet.api.auth.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class TokenBadRequestException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.INVALID_ARGUMENTS;

	public TokenBadRequestException(String message) {
		super(message, responseCode);
	}
}
