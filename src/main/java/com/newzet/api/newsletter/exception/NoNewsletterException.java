package com.newzet.api.newsletter.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class NoNewsletterException extends NewzetException {
	public static final ResponseCode responseCode = ResponseCode.NOT_FOUND;

	public NoNewsletterException(String message) {
		super(message, responseCode);
	}
}
