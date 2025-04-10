package com.newzet.api.auth.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class TokenConflictException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.CONFLICT;

	public TokenConflictException(String message) {
		super(message, responseCode);
	}
}
