package com.newzet.api.auth.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class TokenStolenException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.TOKEN_STOLEN;

	public TokenStolenException(String message) {
		super(message, responseCode);
	}
}
