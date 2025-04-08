package com.newzet.api.newsletter.exception;

import com.newzet.api.common.exception.ResponseCode;
import com.newzet.api.common.exception.ResponseFormat;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NewsletterResponseFormat implements ResponseFormat {

	private final ResponseCode responseCode;
	private final String message;

	public ResponseFormat createResponse(ResponseCode responseCode, String message) {
		return new NewsletterResponseFormat(responseCode, message);
	}

	@Override
	public ResponseCode getResponseCode() {
		return this.responseCode;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
