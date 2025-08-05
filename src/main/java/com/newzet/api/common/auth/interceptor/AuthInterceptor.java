package com.newzet.api.common.auth.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.newzet.api.common.auth.annotation.RequireAuth;
import com.newzet.api.common.auth.business.UserTokenResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
	private final UserTokenResolver userTokenResolver;

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

		userTokenResolver.setTokenInHeader(request);
		return true;
	}
}
