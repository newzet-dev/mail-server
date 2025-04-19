package com.newzet.api.subscription.repository.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class NoSubscriptionException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.NOT_FOUND;

	public NoSubscriptionException(String message) {
		super(message, responseCode);
	}
}
