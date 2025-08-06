package com.newzet.api.common.auth.intercepter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import com.newzet.api.common.auth.annotation.RequireAuth;
import com.newzet.api.common.auth.business.AuthTokenProcessor;
import com.newzet.api.common.auth.interceptor.AuthInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

	private static final String SUBJECT = UUID.randomUUID().toString();
	private static final String NAME = "test";
	private static final Date PAST = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
	private static final Date FUTURE = new Date(System.currentTimeMillis() + 60 * 60 * 1000);

	@InjectMocks
	private AuthInterceptor interceptor;

	@Mock
	private AuthTokenProcessor authTokenProcessor;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private HandlerMethod handlerMethod;

	@Mock
	private RequireAuth requireAuthAnnotation;

	@Test
	@DisplayName("핸들러가 HandlerMethod 타입이 아니면 true를 반환한다")
	void whenNotHandlerMethod_thenReturnsTrue() {
		// Given
		Object handler = new Object();

		// When
		boolean result = interceptor.preHandle(request, response, handler);

		// Then
		assertThat(result).isTrue();
		verifyNoInteractions(authTokenProcessor);
	}

	@Test
	@DisplayName("@RequireAuth 어노테이션이 없으면 true를 반환한다")
	void whenNoRequireAuthAnnotation_thenReturnsTrue() {
		// Given
		when(handlerMethod.getMethodAnnotation(RequireAuth.class)).thenReturn(null);
		when(handlerMethod.getBeanType()).thenReturn((Class)TestController.class);

		// When
		boolean result = interceptor.preHandle(request, response, handlerMethod);

		// Then
		assertThat(result).isTrue();
		verifyNoInteractions(authTokenProcessor);
	}

	@Test
	@DisplayName("어노테이션이 있으면 setTokenInHeader가 호출된다")
	void whenRequireAuth_thenCallsSetTokenInHeader() {
		// Given
		// 1. @RequireAuth(optional=false) 상황 모킹
		when(requireAuthAnnotation.optional()).thenReturn(false);
		when(handlerMethod.getMethodAnnotation(RequireAuth.class)).thenReturn(
			requireAuthAnnotation);
		doNothing().when(authTokenProcessor).setTokenInHeader(request);

		// When
		boolean result = interceptor.preHandle(request, response, handlerMethod);

		// Then
		assertThat(result).isTrue();
		verify(authTokenProcessor, times(1)).setTokenInHeader(request);
		verify(authTokenProcessor, never()).setTokenInHeaderOptional(any());
	}

	@Test
	@DisplayName("optional이 true이면 setTokenInHeaderOptional이 호출된다")
	void whenOptionalAuth_thenCallsSetTokenInHeaderOptional() {
		// Given
		// 1. @RequireAuth(optional=true) 상황 모킹
		when(requireAuthAnnotation.optional()).thenReturn(true);
		when(handlerMethod.getMethodAnnotation(RequireAuth.class)).thenReturn(
			requireAuthAnnotation);
		doNothing().when(authTokenProcessor).setTokenInHeaderOptional(request);

		// When
		boolean result = interceptor.preHandle(request, response, handlerMethod);

		// Then
		assertThat(result).isTrue();
		verify(authTokenProcessor, times(1)).setTokenInHeaderOptional(request);
		verify(authTokenProcessor, never()).setTokenInHeader(any());
	}

	private static class TestController {
		public void testMethod() {
		}
	}
}
