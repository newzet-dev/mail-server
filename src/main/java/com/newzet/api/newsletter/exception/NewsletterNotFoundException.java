package com.newzet.api.newsletter.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class NewsletterNotFoundException extends NewzetException {

	private static final ResponseCode responseCode = ResponseCode.INVALID_ARGUMENTS;

	public NewsletterNotFoundException(String message) {
		super(message, responseCode);
	}

}
