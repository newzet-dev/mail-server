package com.newzet.api.user.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class WithdrawnUserException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.CONFLICT;

	public WithdrawnUserException(String message) {
		super(message, responseCode);
	}
}
