package com.newzet.api.category.jpa.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class NoCategoryException extends NewzetException {
	public static final ResponseCode responseCode = ResponseCode.NOT_FOUND;

	public NoCategoryException(String message) {
		super(message, responseCode);
	}
}
