package com.newzet.api.common.auth.business;

import org.springframework.stereotype.Component;

import com.newzet.api.common.auth.exception.TokenBadRequestException;

@Component
public class AuthorizationHeaderParser {

	private static final String BEARER_PREFIX = "Bearer ";

	public String extractAuthHeader(String authHeader) {
		if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
			throw new TokenBadRequestException("Authorization 헤더가 없거나 Bearer 형식이 아닙니다.");
		}
		String headerValue = authHeader.substring(BEARER_PREFIX.length());
		if (headerValue.isBlank()) {
			throw new TokenBadRequestException("유효하지 않은 토큰입니다.");
		}
		return headerValue;
	}
}
