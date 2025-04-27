package com.newzet.api.auth.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class OAuthErrorException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.OAUTH_ERROR;

	public OAuthErrorException(String message) {
		super(message, responseCode);
	}
}
