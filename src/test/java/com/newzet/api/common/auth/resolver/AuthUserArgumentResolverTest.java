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
import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.auth.domain.Token;

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

	@Test
	@DisplayName("@Login 어노테이션과 AuthUser 타입을 지원한다")
	void whenLoginAndAuthUserType_thenReturnsTrue() {
		// Given
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(true);
		when(parameter.getParameterType()).thenReturn((Class)AuthUser.class);

		// When
		boolean result = resolver.supportsParameter(parameter);

		// Then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("@OptionalLogin 어노테이션과 Optional 타입을 지원한다")
	void whenOptionalLoginAndOptionalType_thenReturnsTrue() {
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
	@DisplayName("지원하지 않는 어노테이션이나 타입은 false를 반환한다")
	void whenUnsupportedType_thenReturnsFalse() {
		// Given
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(true);
		when(parameter.getParameterType()).thenReturn((Class)String.class);

		// When
		boolean result = resolver.supportsParameter(parameter);

		// Then
		assertThat(result).isFalse();
	}

	@Test

	@DisplayName("Optional 타입이 아니면 false를 반환한다")
	void supportsParameter_whenNoOptionalAuthUserType_returnsFalse() {

		// Given
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(false);
		when(parameter.hasParameterAnnotation(OptionalLogin.class)).thenReturn(true);
		when(parameter.getParameterType()).thenReturn((Class)AuthUser.class);

		// When
		boolean result = resolver.supportsParameter(parameter);

		// Then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("@Login, request attribute에 토큰이 있으면 AuthUser를 반환한다")
	void whenLoginAndTokenAttributeExists_thenReturnsAuthUser() {
		// Given
		UUID userId = UUID.randomUUID();
		Token token = Token.of(String.valueOf(userId), new Date(),
			new Date(System.currentTimeMillis() + 3600000));

		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(true);
		when(webRequest.getNativeRequest()).thenReturn(httpRequest);
		when(httpRequest.getAttribute(AUTH_TOKEN_ATTRIBUTE)).thenReturn(token);

		// When
		AuthUser authUser = (AuthUser)resolver.resolveArgument(parameter, mavContainer, webRequest,
			binderFactory);

		// Then
		assertThat(authUser).isNotNull();
		assertThat(authUser.getId()).isEqualTo(userId);
	}

	@Test
	@DisplayName("@Login, request attribute에 토큰이 없으면 null을 반환한다")
	void whenLoginAndTokenAttributeNotExists_thenReturnsNull() {
		// Given
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(true);
		when(webRequest.getNativeRequest()).thenReturn(httpRequest);
		when(httpRequest.getAttribute(AUTH_TOKEN_ATTRIBUTE)).thenReturn(null);

		// When & Then
		assertNull(resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory));
	}

	@Test
	@DisplayName("@OptionalLogin, request attribute에 토큰이 있으면 Optional<AuthUser>를 반환한다")
	void whenOptionalLoginAndTokenAttributeExists_thenReturnsOptionalOfAuthUser() {
		// Given
		UUID userId = UUID.randomUUID();
		Token token = Token.of(String.valueOf(userId), new Date(),
			new Date(System.currentTimeMillis() + 3600000));

		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(false); // @OptionalLogin 케이스
		when(webRequest.getNativeRequest()).thenReturn(httpRequest);
		when(httpRequest.getAttribute(AUTH_TOKEN_ATTRIBUTE)).thenReturn(token);

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
	@DisplayName("@OptionalLogin, request attribute에 토큰이 없으면 Optional.empty를 반환한다")
	void whenOptionalLoginAndTokenAttributeNotExists_thenReturnsOptionalEmpty() {
		// Given
		when(parameter.hasParameterAnnotation(Login.class)).thenReturn(false); // @OptionalLogin 케이스
		when(webRequest.getNativeRequest()).thenReturn(httpRequest);
		when(httpRequest.getAttribute(AUTH_TOKEN_ATTRIBUTE)).thenReturn(null);

		// When
		Object result = resolver.resolveArgument(parameter, mavContainer, webRequest,
			binderFactory);

		// Then
		assertThat(result).isEqualTo(Optional.empty());
	}

}
