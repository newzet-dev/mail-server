package com.newzet.api.userinfo.exception;

import com.newzet.api.common.exception.NewzetException;
import com.newzet.api.common.response.ResponseCode;

public class AuthUserFailException extends NewzetException {
	private static final ResponseCode responseCode = ResponseCode.SUPABASE_ERROR;

	public AuthUserFailException(String message) {
		super(message, responseCode);
	}
}