package com.newzet.api.common.auth.business;

import org.springframework.stereotype.Component;

import com.newzet.api.common.auth.domain.Token;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserTokenResolver {
	private static final String AUTH_TOKEN_ATTRIBUTE = "AUTH_TOKEN";
	private final AuthorizationHeaderParser authorizationHeaderParser;
	private final TokenValidator tokenValidator;
	private final TokenConverter tokenConverter;

	public void setTokenInHeader(HttpServletRequest request) {
		String headerValue = getAuthorizationHeader(request);
		Token token = convertAndValidateToken(headerValue);
		request.setAttribute(AUTH_TOKEN_ATTRIBUTE, token);
	}

	public void setTokenInHeaderOptional(HttpServletRequest request) {
		if (!isExistsAuthorizationHeader(request)) {
			return;
		}
		String headerValue = getAuthorizationHeader(request);
		Token token = convertAndValidateToken(headerValue);
		request.setAttribute(AUTH_TOKEN_ATTRIBUTE, token);
	}

	private String getAuthorizationHeader(HttpServletRequest request) {
		return authorizationHeaderParser.extractAuthHeader(
			request.getHeader("Authorization"));
	}

	private Token convertAndValidateToken(String headerValue) {
		Token token = tokenConverter.toToken(headerValue);
		tokenValidator.validate(token);
		return token;
	}

	private boolean isExistsAuthorizationHeader(HttpServletRequest request) {
		return request.getHeader("Authorization") != null;
	}
}
