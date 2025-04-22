package com.newzet.api.common.util.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class UuidConvertFailException extends NewzetException {

	private static final ResponseCode responseCode = ResponseCode.INVALID_ARGUMENTS;

	public UuidConvertFailException(String message) {
		super(message, responseCode);
	}
}
