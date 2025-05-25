package com.newzet.api.common.auth.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.newzet.api.common.auth.annotation.RequireAuth;
import com.newzet.api.common.auth.business.AuthorizationHeaderParser;
import com.newzet.api.common.auth.business.TokenConverter;
import com.newzet.api.common.auth.business.TokenValidator;
import com.newzet.api.common.auth.domain.Token;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
	private static final String AUTH_TOKEN_ATTRIBUTE = "AUTH_TOKEN";

	private final AuthorizationHeaderParser authorizationHeaderParser;
	private final TokenValidator tokenValidator;
	private final TokenConverter tokenConverter;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
		Object handler) {
		if (!(handler instanceof HandlerMethod handlerMethod)) {
			return true;
		}

		boolean requiresAuth = handlerMethod.hasMethodAnnotation(RequireAuth.class) ||
			handlerMethod.getBeanType().isAnnotationPresent(RequireAuth.class);

		if (!requiresAuth) {
			return true;
		}

		String headerValue = authorizationHeaderParser.extractAuthHeader(
			request.getHeader("Authorization"));
		Token token = tokenConverter.toToken(headerValue);
		tokenValidator.validate(token);
		request.setAttribute(AUTH_TOKEN_ATTRIBUTE, token);
		return true;
	}
}
