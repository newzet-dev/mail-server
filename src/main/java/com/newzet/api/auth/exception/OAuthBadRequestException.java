package com.newzet.api.auth.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class OAuthBadRequestException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.INVALID_ARGUMENTS;

	public OAuthBadRequestException(String message) {
		super(message, responseCode);
	}
}
