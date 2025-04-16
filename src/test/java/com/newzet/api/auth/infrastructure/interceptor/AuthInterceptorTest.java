package com.newzet.api.auth.infrastructure.interceptor;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import com.newzet.api.auth.business.service.JwtFactory;
import com.newzet.api.auth.business.validator.JwtValidator;
import com.newzet.api.auth.domain.Token;
import com.newzet.api.auth.domain.TokenType;
import com.newzet.api.auth.exception.TokenBadRequestException;
import com.newzet.api.auth.infrastructure.annotation.RequireAuth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthInterceptorTest {

	private static final String AUTH_TOKEN_ATTRIBUTE = "AUTH_TOKEN";

	@InjectMocks
	private AuthInterceptor interceptor;

	@Mock
	private JwtFactory jwtFactory;

	@Mock
	private JwtValidator JWTValidator;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private HandlerMethod handlerMethod;

	private Token token;
	private UUID userId;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
		token = Token.of(TokenType.ACCESS, "token-value", userId.toString(), new Date(),
			new Date(System.currentTimeMillis() + 3600000));
	}

	@Test
	public void preHandle_whenNotHandlerMethod_returnsTrue() throws Exception {
		// Given
		Object handler = new Object();

		// When
		boolean result = interceptor.preHandle(request, response, handler);

		// Then
		assertThat(result).isTrue();
		verifyNoInteractions(jwtFactory, JWTValidator);
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
		verifyNoInteractions(jwtFactory, JWTValidator);
	}

	@Test
	public void preHandle_whenMethodRequiresAuth_validatesToken() {
		// Given
		when(handlerMethod.hasMethodAnnotation(RequireAuth.class)).thenReturn(true);
		when(request.getHeader("Authorization")).thenReturn("Bearer token-value");
		when(jwtFactory.parseToken("token-value")).thenReturn(Optional.of(token));
		doNothing().when(JWTValidator).validateAccessToken(token);

		// When
		boolean result = interceptor.preHandle(request, response, handlerMethod);

		// Then
		assertThat(result).isTrue();
		verify(request).setAttribute(AUTH_TOKEN_ATTRIBUTE, token);
	}

	@Test
	public void preHandle_whenClassRequiresAuth_validatesToken() {
		// Given
		when(handlerMethod.hasMethodAnnotation(RequireAuth.class)).thenReturn(false);
		when(handlerMethod.getBeanType()).thenReturn((Class)SecuredController.class);
		when(request.getHeader("Authorization")).thenReturn("Bearer token-value");
		when(jwtFactory.parseToken("token-value")).thenReturn(Optional.of(token));
		doNothing().when(JWTValidator).validateAccessToken(token);

		// When
		boolean result = interceptor.preHandle(request, response, handlerMethod);

		// Then
		assertThat(result).isTrue();
		verify(request).setAttribute(AUTH_TOKEN_ATTRIBUTE, token);
	}

	@Test
	public void preHandle_whenNoAuthHeader_throwsJWTBadRequestException() {
		// Given
		when(handlerMethod.hasMethodAnnotation(RequireAuth.class)).thenReturn(true);
		when(request.getHeader("Authorization")).thenReturn(null);

		// When & Then
		assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
			.isInstanceOf(TokenBadRequestException.class);
	}

	@Test
	public void preHandle_whenInvalidBearerFormat_throwsJWTBadRequestException() {
		// Given
		when(handlerMethod.hasMethodAnnotation(RequireAuth.class)).thenReturn(true);
		when(request.getHeader("Authorization")).thenReturn("Invalid token-value");

		// When & Then
		assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
			.isInstanceOf(TokenBadRequestException.class);
	}

	@Test
	public void preHandle_whenInvalidToken_throwsJWTBadRequestException() {
		// Given
		when(handlerMethod.hasMethodAnnotation(RequireAuth.class)).thenReturn(true);
		when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
		when(jwtFactory.parseToken("invalid-token")).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> interceptor.preHandle(request, response, handlerMethod))
			.isInstanceOf(TokenBadRequestException.class);
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
