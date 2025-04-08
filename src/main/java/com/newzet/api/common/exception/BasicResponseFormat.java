package com.newzet.api.common.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BasicResponseFormat implements ResponseFormat {

	private final ResponseCode responseCode;
	private final String message;

	public ResponseFormat createResponse(ResponseCode responseCode, String message) {
		return new BasicResponseFormat(responseCode, message);
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
