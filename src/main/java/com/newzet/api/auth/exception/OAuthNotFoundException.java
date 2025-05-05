package com.newzet.api.auth.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class OAuthNotFoundException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.NOT_FOUND;

	public OAuthNotFoundException(String message) {
		super(message, responseCode);
	}
}
