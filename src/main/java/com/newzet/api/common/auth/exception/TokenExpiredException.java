package com.newzet.api.common.auth.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class TokenExpiredException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.TOKEN_EXPIRED;

	public TokenExpiredException(String message) {
		super(message, responseCode);
	}
}
