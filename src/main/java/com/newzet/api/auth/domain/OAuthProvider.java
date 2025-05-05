package com.newzet.api.auth.domain;

import com.newzet.api.auth.exception.OAuthBadRequestException;

public enum OAuthProvider {
	KAKAO, APPLE,
	UNSUPPORTED;

	public static OAuthProvider fromString(String value) {
		if (value == null || value.isEmpty()) {
			throw new OAuthBadRequestException("Provider 값이 없습니다.");
		}

		try {
			return OAuthProvider.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new OAuthBadRequestException("올바른 Provider가 아닙니다.");
		}
	}
}
