package com.newzet.api.auth.infrastructure.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.newzet.api.auth.business.service.JwtFactory;
import com.newzet.api.auth.business.validator.JwtValidator;
import com.newzet.api.auth.domain.Token;
import com.newzet.api.auth.exception.JWTBadRequestException;
import com.newzet.api.auth.infrastructure.annotation.RequireAuth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
	private static final String AUTH_TOKEN_ATTRIBUTE = "AUTH_TOKEN";
	private static final String BEARER_PREFIX = "Bearer ";
	private final JwtFactory jwtFactory;
	private final JwtValidator JWTValidator;

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

		String tokenValue = extractTokenFromHeader(request);
		Token token = jwtFactory.parseToken(tokenValue)
			.orElseThrow(() -> new JWTBadRequestException("유효하지 않은 토큰입니다."));

		JWTValidator.validateAccessToken(token);

		request.setAttribute(AUTH_TOKEN_ATTRIBUTE, token);

		return true;
	}

	private String extractTokenFromHeader(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
			throw new JWTBadRequestException("Authorization 헤더가 없거나 Bearer 형식이 아닙니다.");
		}
		return authHeader.substring(BEARER_PREFIX.length());
	}
}
