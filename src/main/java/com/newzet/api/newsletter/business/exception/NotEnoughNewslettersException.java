package com.newzet.api.newsletter.business.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class NotEnoughNewslettersException extends NewzetException {
	public static final ResponseCode responseCode = ResponseCode.SERVER_ERROR;

	public NotEnoughNewslettersException(String message) {
		super(message, responseCode);
	}
}
