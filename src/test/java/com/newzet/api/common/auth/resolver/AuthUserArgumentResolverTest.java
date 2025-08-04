package com.newzet.api.common.auth.resolver;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.newzet.api.common.auth.annotation.Login;
import com.newzet.api.common.auth.annotation.OptionalLogin;
import com.newzet.api.common.auth.business.AuthorizationHeaderParser;
import com.newzet.api.common.auth.business.TokenConverter;
import com.newzet.api.common.auth.business.TokenValidator;
import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.auth.domain.Token;
import com.newzet.api.common.auth.exception.TokenBadRequestException;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AuthUserArgumentResolverTest {

	private static final String AUTH_TOKEN_ATTRIBUTE = "AUTH_TOKEN";

	@InjectMocks
	private AuthUserArgumentResolver resolver;

	@Mock
	private MethodParameter parameter;

	@Mock
	private ModelAndViewContainer mavContainer;

	@Mock
	private NativeWebRequest webRequest;

	@Mock
	private WebDataBinderFactory binderFactory;

	@Mock
	private HttpServletRequest httpRequest;

	@Mock
	private AuthorizationHeaderParser authorizationHeaderParser;

	@Mock
	private TokenConverter tokenConverter;

	@Mock
	private TokenValidator tokenValidator;

	@Test
	public void supportsParameter_whenHasLoginAnnotationAndAuthUserType_returnsTrue() {
		// Given
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(true);
		when(parameter.getParameterType()).thenReturn((Class)AuthUser.class);

		// When
		boolean result = resolver.supportsParameter(parameter);

		// Then
		assertThat(result).isTrue();
	}

	@Test
	public void supportsParameter_whenNoLoginAnnotation_returnsFalse() {
		// Given
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(false);
		when(parameter.getParameterType()).thenReturn((Class)AuthUser.class);

		// When
		boolean result = resolver.supportsParameter(parameter);

		// Then
		assertThat(result).isFalse();
	}

	@Test
	public void supportsParameter_whenNotAuthUserType_returnsFalse() {
		// Given
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(true);
		when(parameter.getParameterType()).thenReturn((Class)String.class);

		// When
		boolean result = resolver.supportsParameter(parameter);

		// Then
		assertThat(result).isFalse();
	}

	@Test
	public void resolveArgument_whenTokenExists_returnsAuthUser() {
		// Given
		UUID userId = UUID.randomUUID();
		Date expiryDate = new Date(System.currentTimeMillis() + 3600000);
		Token token = Token.of(String.valueOf(userId), "testName", new Date(),
			expiryDate);

		when(webRequest.getNativeRequest()).thenReturn(httpRequest);
		when(httpRequest.getAttribute(AUTH_TOKEN_ATTRIBUTE)).thenReturn(token);
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(true);

		// When
		AuthUser authUser = (AuthUser)resolver.resolveArgument(parameter, mavContainer, webRequest,
			binderFactory);

		// Then
		assertThat(authUser).isNotNull();
		assertThat(authUser.getId()).isEqualTo(userId);
	}

	@Test
	public void resolveArgument_whenTokenNotExists_returnNull() {
		// Given
		when(webRequest.getNativeRequest()).thenReturn(httpRequest);
		when(httpRequest.getAttribute(AUTH_TOKEN_ATTRIBUTE)).thenReturn(null);
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(true);

		// When & Then
		assertNull(resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory));
	}

	@Test
	@DisplayName("@OptionalLogin, Optional 타입일 때 true를 반환한다")
	void supportsParameter_whenHasOptionalLoginAnnotationAndOptionalType_returnsTrue() {
		// Given
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(false);
		when(parameter.hasParameterAnnotation(OptionalLogin.class)).thenReturn(true);
		when(parameter.getParameterType()).thenReturn((Class)Optional.class);

		// When
		boolean result = resolver.supportsParameter(parameter);

		// Then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("@OptionalLogin 어노테이션이 없으면 false를 반환한다")
	void supportsParameter_whenNoOptionalLoginAnnotationButOptionalType_returnsFalse() {
		// Given
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(false);
		when(parameter.hasParameterAnnotation(OptionalLogin.class)).thenReturn(false);
		when(parameter.getParameterType()).thenReturn((Class)Optional.class);

		// When
		boolean result = resolver.supportsParameter(parameter);

		// Then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("@OptionalLogin, 유효한 토큰이 헤더에 있으면 Optional<AuthUser>를 반환한다")
	void resolveArgument_whenOptionalLoginAndTokenExists_returnsOptionalOfAuthUser() {
		// Given
		UUID userId = UUID.randomUUID();
		String header = "Bearer test.token.value";
		String tokenValue = "test.token.value";
		Token token = Token.of(String.valueOf(userId), "testUser", new Date(),
			new Date(System.currentTimeMillis() + 3600000));

		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(false);
		when(webRequest.getNativeRequest()).thenReturn(httpRequest);
		when(httpRequest.getHeader("Authorization")).thenReturn(header);
		when(authorizationHeaderParser.extractAuthHeader(header)).thenReturn(tokenValue);
		when(tokenConverter.toToken(tokenValue)).thenReturn(token);
		doNothing().when(tokenValidator).validate(token);

		// When
		Object result = resolver.resolveArgument(parameter, mavContainer, webRequest,
			binderFactory);

		// Then
		assertThat(result).isInstanceOf(Optional.class);
		Optional<AuthUser> optionalAuthUser = (Optional<AuthUser>)result;
		assertThat(optionalAuthUser).isPresent();
		assertThat(optionalAuthUser.get().getId()).isEqualTo(userId);
	}

	@Test
	@DisplayName("@OptionalLogin, 토큰 헤더가 없으면 Optional.empty를 반환한다")
	void resolveArgument_whenOptionalLoginAndNoToken_returnsOptionalEmpty() {
		// Given
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(false);
		when(webRequest.getNativeRequest()).thenReturn(httpRequest);
		when(httpRequest.getHeader("Authorization")).thenReturn(null);
		when(authorizationHeaderParser.extractAuthHeader(null)).thenReturn(null);

		// When
		Object result = resolver.resolveArgument(parameter, mavContainer, webRequest,
			binderFactory);

		// Then
		assertThat(result).isEqualTo(Optional.empty());
	}

	@Test
	@DisplayName("@OptionalLogin, 토큰이 유효하지 않으면 예외를 던진다")
	void resolveArgument_whenOptionalLoginAndInvalidToken_throwsException() {
		// Given
		String header = "Bearer invalid.token.value";
		String tokenValue = "invalid.token.value";
		Token token = Token.of(UUID.randomUUID().toString(), "testUser", new Date(),
			new Date(System.currentTimeMillis() + 3600000));

		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(false);
		when(webRequest.getNativeRequest()).thenReturn(httpRequest);
		when(httpRequest.getHeader("Authorization")).thenReturn(header);
		when(authorizationHeaderParser.extractAuthHeader(header)).thenReturn(tokenValue);
		when(tokenConverter.toToken(tokenValue)).thenReturn(token);

		doThrow(new TokenBadRequestException("토큰 생성에 필요한 값이 누락되었습니다."))
			.when(tokenValidator).validate(token);

		// When & Then
		assertThrows(TokenBadRequestException.class, () -> {
			resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
		});
	}
}
