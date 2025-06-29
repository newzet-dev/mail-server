package com.newzet.api.fcm.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class NoFcmTokenException extends NewzetException {
	public static final ResponseCode responseCode = ResponseCode.NOT_FOUND;

	public NoFcmTokenException(String message) {
		super(message, responseCode);
	}
}

