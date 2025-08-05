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

	//TODO: OptionalAuth랑 분리하기(뉴스레터 상세정보 조회)
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
		Token token = (Token)request.getAttribute(AUTH_TOKEN_ATTRIBUTE);
		if (token == null) {
			return null;
		}
		return new AuthUser(token.getSubject());
	}
}
