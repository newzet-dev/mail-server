package com.newzet.api.common.auth.intercepter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import com.newzet.api.common.auth.annotation.RequireAuth;
import com.newzet.api.common.auth.business.UserTokenResolver;
import com.newzet.api.common.auth.domain.Token;
import com.newzet.api.common.auth.interceptor.AuthInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

	private static final String SUBJECT = UUID.randomUUID().toString();
	private static final String NAME = "test";
	private static final Date PAST = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
	private static final Date FUTURE = new Date(System.currentTimeMillis() + 60 * 60 * 1000);

	private static final String AUTH_TOKEN_ATTRIBUTE = "AUTH_TOKEN";

	@InjectMocks
	private AuthInterceptor interceptor;

	@Mock
	private UserTokenResolver userTokenResolver;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private HandlerMethod handlerMethod;

	@Test
	public void preHandle_whenNotHandlerMethod_returnsTrue() throws Exception {
		// Given
		Object handler = new Object();

		// When
		boolean result = interceptor.preHandle(request, response, handler);

		// Then
		Assertions.assertTrue(result);
		verifyNoInteractions(userTokenResolver);
	}

	@Test
	public void preHandle_whenNoRequiredAuth_returnsTrue() {
		// Given
		when(handlerMethod.hasMethodAnnotation(RequireAuth.class)).thenReturn(false);
		when(handlerMethod.getBeanType()).thenReturn((Class)TestController.class);

		// When
		boolean result = interceptor.preHandle(request, response, handlerMethod);

		// Then
		assertThat(result).isTrue();
		verifyNoInteractions(userTokenResolver);
	}

	@Test
	public void preHandle_whenRequiresAuthAnnotation_validateToken() {
		// Given
		Token token = Token.of(SUBJECT, NAME, PAST, FUTURE);
		when(handlerMethod.hasMethodAnnotation(RequireAuth.class)).thenReturn(false);
		when(handlerMethod.getBeanType()).thenReturn((Class)SecuredController.class);
		doNothing().when(userTokenResolver).setTokenInHeader(any());

		// When
		boolean result = interceptor.preHandle(request, response, handlerMethod);

		// Then
		verify(userTokenResolver, times(1)).setTokenInHeader(any());
		assertThat(result).isTrue();
	}

	@Test
	public void preHandle_whenRequiresAuthMethodAnnotation_validateToken() {
		// Given
		Token token = Token.of(SUBJECT, NAME, PAST, FUTURE);
		when(handlerMethod.hasMethodAnnotation(RequireAuth.class)).thenReturn(true);
		doNothing().when(userTokenResolver).setTokenInHeader(any());

		// When
		boolean result = interceptor.preHandle(request, response, handlerMethod);

		// Then
		verify(userTokenResolver, times(1)).setTokenInHeader(any());
		assertThat(result).isTrue();
	}

	private static class TestController {
		public void testMethod() {
		}
	}

	@RequireAuth
	private static class SecuredController {
		public void testMethod() {
		}
	}
}
