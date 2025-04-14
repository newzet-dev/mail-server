package com.newzet.api.common.exception;

import com.newzet.api.common.response.ResponseCode;

public class InternalErrorException extends NewzetException {
	public static final ResponseCode responseCode = ResponseCode.SERVER_ERROR;
	public static final String message = "내부에서 요청 처리에 실패하였습니다. 다시 시도해주세요.";

	public InternalErrorException() {
		super(message, responseCode);
	}
}
