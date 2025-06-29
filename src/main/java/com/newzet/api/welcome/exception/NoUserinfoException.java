package com.newzet.api.welcome.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class NoUserinfoException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.NOT_FOUND;

	public NoUserinfoException(String message) {
		super(message, responseCode);
	}
}
