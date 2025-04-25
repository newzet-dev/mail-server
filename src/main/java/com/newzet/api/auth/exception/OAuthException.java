package com.newzet.api.auth.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class OAuthException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.INVALID_ARGUMENTS;

	public OAuthException(String message) {
		super(message, responseCode);
	}
}
