package com.newzet.api.common.auth.resolver;

import java.util.Optional;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.newzet.api.common.auth.annotation.Login;
import com.newzet.api.common.auth.annotation.OptionalLogin;
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
		Class<?> parameterType = parameter.getParameterType();

		boolean hasAuthenticatedUserAnnotation = parameter.hasParameterAnnotation(Login.class);
		boolean hasAuthUserParameterType = parameterType.equals(AuthUser.class);

		boolean hasOptionalAuthenticatedUserAnnotation = parameter.hasParameterAnnotation(
			OptionalLogin.class);
		boolean hasOptionalParameterType = parameterType.equals(Optional.class);

		return (hasAuthenticatedUserAnnotation && hasAuthUserParameterType) || (
			hasOptionalAuthenticatedUserAnnotation && hasOptionalParameterType);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();

		Token token = (Token)request.getAttribute(AUTH_TOKEN_ATTRIBUTE);
		if (parameter.hasParameterAnnotation(Login.class)) {
			if (token == null) {
				return null;
			}
			return new AuthUser(token.getSubject());
		} else {
			if (token == null) {
				return Optional.empty();
			}
			return Optional.of(new AuthUser(token.getSubject()));
		}
	}
}
