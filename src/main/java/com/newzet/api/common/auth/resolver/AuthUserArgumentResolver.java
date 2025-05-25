package com.newzet.api.common.auth.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.newzet.api.common.auth.annotation.Login;
import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.auth.domain.Token;
import com.newzet.api.common.auth.exception.TokenBadRequestException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {
	private static final String AUTH_TOKEN_ATTRIBUTE = "AUTH_TOKEN";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		boolean hasAuthenticatedUserAnnotation = parameter.hasMethodAnnotation(Login.class);
		boolean hasAuthUserParameterType = parameter.getParameterType().equals(AuthUser.class);

		return hasAuthenticatedUserAnnotation && hasAuthUserParameterType;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		Token token = (Token)request.getAttribute(AUTH_TOKEN_ATTRIBUTE);

		if (token == null) {
			throw new TokenBadRequestException(
				"인증 정보를 찾을 수 없습니다. 인증이 필요한 로직인 경우 @requiresAuth 를 추가하세요.");
		}

		return buildAuthUserFromToken(token);
	}

	private AuthUser buildAuthUserFromToken(Token token) {
		return AuthUser.from(token);
	}
}
