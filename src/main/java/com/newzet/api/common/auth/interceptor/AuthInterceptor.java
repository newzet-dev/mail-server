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

		RequireAuth requireAuth = handlerMethod.getMethodAnnotation(RequireAuth.class);
		if (requireAuth == null) {
			requireAuth = handlerMethod.getBeanType().getAnnotation(RequireAuth.class);
		}

		// @RequireAuth가 기재되지 않음을 최종 확인
		if (requireAuth == null) {
			return true;
		}

		// @RequireAuth optional 속성값 확인
		boolean isOptional = requireAuth.optional();

		if (!isOptional) {
			userTokenResolver.setTokenInHeader(request); // 로그인만
		} else {
			userTokenResolver.setTokenInHeaderOptional(request); // 로그인&비로그인 혼용
		}

		return true;
	}
}
