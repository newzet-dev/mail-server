package com.newzet.api.article.repository.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class NoArticleException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.NOT_FOUND;

	public NoArticleException(String message) {
		super(message, responseCode);
	}
}
