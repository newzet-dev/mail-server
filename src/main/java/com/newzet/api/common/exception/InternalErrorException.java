package com.newzet.api.common.exception;

import com.newzet.api.common.response.ResponseCode;

public class InternalErrorException extends NewzetException {
	public static final ResponseCode responseCode = ResponseCode.SERVER_ERROR;

	public InternalErrorException(String message) {
		super(message, responseCode);
	}
}
