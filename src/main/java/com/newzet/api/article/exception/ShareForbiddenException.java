package com.newzet.api.article.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class ShareForbiddenException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.FORBIDDEN;

	public ShareForbiddenException(String message) {
		super(message, responseCode);
	}
}


