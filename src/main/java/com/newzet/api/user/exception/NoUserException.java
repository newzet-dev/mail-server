package com.newzet.api.user.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class NoUserException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.NOT_FOUND;

	public NoUserException(String message) {
		super(message, responseCode);
	}
}
