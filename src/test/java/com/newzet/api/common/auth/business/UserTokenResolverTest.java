package com.newzet.api.common.auth.business;

import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.common.auth.domain.Token;
import com.newzet.api.common.auth.exception.TokenBadRequestException;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class UserTokenResolverTest {

	private static final String AUTH_TOKEN_ATTRIBUTE = "AUTH_TOKEN";

	@InjectMocks
	private UserTokenResolver userTokenResolver;

	@Mock
	private AuthorizationHeaderParser authorizationHeaderParser;

	@Mock
	private TokenValidator tokenValidator;

	@Mock
	private TokenConverter tokenConverter;

	@Mock
	private HttpServletRequest request;

	@Test
	@DisplayName("유효한 Authorization 헤더가 있으면 토큰을 생성해 request attribute에 저장한다.")
	void withValidHeader_thenSetsTokenAttribute() {
		// Given
		String fullHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
		String extractedTokenValue = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

		UUID userId = UUID.randomUUID();
		Token mockToken = Token.of(String.valueOf(userId), "testUser", new Date(), new Date());

		when(request.getHeader("Authorization")).thenReturn(fullHeader);
		when(authorizationHeaderParser.extractAuthHeader(fullHeader)).thenReturn(
			extractedTokenValue);
		when(tokenConverter.toToken(extractedTokenValue)).thenReturn(mockToken);
		doNothing().when(tokenValidator).validate(mockToken);

		// When
		userTokenResolver.setTokenInHeader(request);

		// Then
		// 1. request.setAttribute가 올바른 이름과 토큰으로 호출되었는지 검증
		verify(request, times(1)).setAttribute(AUTH_TOKEN_ATTRIBUTE, mockToken);

		// 2. 모든 의존성 메서드가 정확히 1번씩 호출되었는지 검증
		verify(authorizationHeaderParser, times(1)).extractAuthHeader(fullHeader);
		verify(tokenConverter, times(1)).toToken(extractedTokenValue);
		verify(tokenValidator, times(1)).validate(mockToken);
	}

	@Test
	@DisplayName("유효한 Authorization 헤더가 있으면 토큰을 생성해 request attribute에 저장한다. (Optional 메서드 호출)")
	void withValidHeader_thenSetsTokenAttribute_for_optional_auth() {
		// Given
		String fullHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
		String extractedTokenValue = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

		UUID userId = UUID.randomUUID();
		Token mockToken = Token.of(String.valueOf(userId), "testUser", new Date(), new Date());

		when(request.getHeader("Authorization")).thenReturn(fullHeader);
		when(authorizationHeaderParser.extractAuthHeader(fullHeader)).thenReturn(
			extractedTokenValue);
		when(tokenConverter.toToken(extractedTokenValue)).thenReturn(mockToken);
		doNothing().when(tokenValidator).validate(mockToken);

		// When
		userTokenResolver.setTokenInHeaderOptional(request);

		// Then
		// 1. request.setAttribute가 올바른 이름과 토큰으로 호출되었는지 검증
		verify(request, times(1)).setAttribute(AUTH_TOKEN_ATTRIBUTE, mockToken);

		// 2. 모든 의존성 메서드가 정확히 1번씩 호출되었는지 검증
		verify(authorizationHeaderParser, times(1)).extractAuthHeader(fullHeader);
		verify(tokenConverter, times(1)).toToken(extractedTokenValue);
		verify(tokenValidator, times(1)).validate(mockToken);
	}

	@Test
	@DisplayName("Authorization 헤더가 없으면 NullPointerException이 발생한다.")
	void withoutHeader_thenThrowsException() {
		// Given
		when(request.getHeader("Authorization")).thenReturn(null);
		when(authorizationHeaderParser.extractAuthHeader(null)).thenReturn(null);

		// toToken(null) 호출 시 예외가 발생하도록 설정
		when(tokenConverter.toToken(null)).thenThrow(
			new IllegalArgumentException("토큰 값은 null일 수 없습니다."));

		// When & Then
		assertThrows(IllegalArgumentException.class, () -> {
			userTokenResolver.setTokenInHeader(request);
		});

		// setAttribute는 절대 호출되면 안 됨
		verify(request, never()).setAttribute(anyString(), any());
	}

	@Test
	@DisplayName("헤더가 없어도 Optional 대상은 예외 없이 정상 종료된다")
	void withoutHeader_for_optional_auth_thenReturnsGracefully() {
		// Given
		when(request.getHeader("Authorization")).thenReturn(null);

		// When & Then
		// 예외가 발생하지 않는 것을 검증
		assertDoesNotThrow(() -> userTokenResolver.setTokenInHeaderOptional(request));

		// 다른 메서드들이 호출되지 않았는지 검증
		verify(tokenConverter, never()).toToken(any());
		verify(tokenValidator, never()).validate(any());
		verify(request, never()).setAttribute(anyString(), any());
	}

	@Test
	@DisplayName("토큰이 유효하지 않으면 Validator가 던진 예외를 그대로 전파한다.")
	void withInvalidToken_thenPropagatesException() {
		// Given
		String fullHeader = "Bearer invalid.token.string";
		String extractedTokenValue = "invalid.token.string";
		UUID userId = UUID.randomUUID();
		Token invalidMockToken = Token.of(String.valueOf(userId), "testUser", new Date(),
			new Date());

		when(request.getHeader("Authorization")).thenReturn(fullHeader);
		when(authorizationHeaderParser.extractAuthHeader(fullHeader)).thenReturn(
			extractedTokenValue);
		when(tokenConverter.toToken(extractedTokenValue)).thenReturn(invalidMockToken);

		doThrow(new TokenBadRequestException("유효하지 않은 토큰입니다."))
			.when(tokenValidator).validate(invalidMockToken);

		// When & Then
		assertThrows(TokenBadRequestException.class, () ->
			userTokenResolver.setTokenInHeader(request));

		// setAttribute는 절대 호출되면 안 됨
		verify(request, never()).setAttribute(anyString(), any());
	}

}
