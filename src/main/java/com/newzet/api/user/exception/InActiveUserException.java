package com.newzet.api.user.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class InActiveUserException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.CONFLICT;

	public InActiveUserException(String message) {
		super(message, responseCode);
	}
}
