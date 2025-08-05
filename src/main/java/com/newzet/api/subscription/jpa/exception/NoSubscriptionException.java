package com.newzet.api.subscription.jpa.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class NoSubscriptionException extends NewzetException {
	private static ResponseCode responseCode = ResponseCode.NOT_FOUND;

	public NoSubscriptionException(String message) {
		super(message, responseCode);
	}
}
