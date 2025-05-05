package com.newzet.api.auth.business.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.newzet.api.auth.business.dto.JwtResponse;
import com.newzet.api.auth.business.dto.OAuthLoginResponse;
import com.newzet.api.auth.business.dto.OAuthMappingEntityDto;
import com.newzet.api.auth.business.dto.OAuthTokenDto;
import com.newzet.api.auth.business.dto.TokenDTO;
import com.newzet.api.auth.business.service.oauth.OAuthRepository;
import com.newzet.api.auth.business.service.oauth.OAuthService;
import com.newzet.api.auth.domain.DeviceType;
import com.newzet.api.auth.domain.OAuthMapping;
import com.newzet.api.auth.domain.OAuthProvider;
import com.newzet.api.auth.domain.OAuthToken;
import com.newzet.api.auth.domain.OAuthUserInfo;
import com.newzet.api.auth.domain.Token;
import com.newzet.api.auth.domain.TokenType;
import com.newzet.api.auth.exception.OAuthBadRequestException;
import com.newzet.api.auth.exception.OAuthNotFoundException;
import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.user.business.service.UserFactory;
import com.newzet.api.user.business.service.UserRepository;
import com.newzet.api.user.domain.User;
import com.newzet.api.user.domain.UserStatus;
import com.newzet.api.user.exception.NoUserException;

@ExtendWith(MockitoExtension.class)
class OAuthLoginServiceTest {

	private final UUID userId = UUID.randomUUID();
	private final UUID oAuthMappingEntityId = UUID.randomUUID();
	@Mock
	private OAuthRepository oAuthRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private JwtFactory jwtFactory;
	@Mock
	private TokenRepository tokenRepository;
	@Mock
	private OAuthService kakaoOAuthService;
	@Mock
	private OAuthService googleOAuthService;
	@InjectMocks
	private OAuthLoginService oAuthLoginService;

	@BeforeEach
	void setUp() {
		Map<String, OAuthService> oAuthServiceMap = new HashMap<>();
		oAuthServiceMap.put("kakaoOAuthService", kakaoOAuthService);
		oAuthServiceMap.put("googleOAuthService", googleOAuthService);

		ReflectionTestUtils.setField(oAuthLoginService, "oAuthServices", oAuthServiceMap);
		ReflectionTestUtils.setField(oAuthLoginService, "webRedirectUri",
			"http://test/oauth/callback");
		ReflectionTestUtils.setField(oAuthLoginService, "appRedirectUri", "myapp://oauth/callback");
	}

	@Test
	void getOAuthLoginUrl_ShouldReturnCorrectUrl() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		DeviceType deviceType = DeviceType.APP;
		when(kakaoOAuthService.isBackendRedirect()).thenReturn(true);
		when(kakaoOAuthService.getRedirectUrl(deviceType.name())).thenReturn(
			"https://kauth.kakao.com/oauth/authorize?...");

		// When
		String url = oAuthLoginService.getOAuthLoginUrl(provider.name(), deviceType.name());

		// Then
		assertThat(url).isEqualTo("https://kauth.kakao.com/oauth/authorize?...");
		verify(kakaoOAuthService).isBackendRedirect();
		verify(kakaoOAuthService).getRedirectUrl(deviceType.name());
	}

	@Test
	void getOAuthLoginUrl_WhenProviderDoesNotSupportBackendRedirect_ThenThrowsException() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		DeviceType deviceType = DeviceType.WEB;
		when(kakaoOAuthService.isBackendRedirect()).thenReturn(false);

		// When, Then
		assertThatExceptionOfType(OAuthBadRequestException.class)
			.isThrownBy(() -> oAuthLoginService.getOAuthLoginUrl(provider.name(), deviceType.name()))
			.withMessage("이 제공자는 백엔드 리다이렉트를 지원하지 않습니다.");
	}

	@Test
	void handleOAuthCallback_WhenExistingUser_ThenRedirectWithTokens() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		DeviceType deviceType = DeviceType.WEB;

		OAuthUserInfo userInfo = mockOAuthUserInfo();
		OAuthMappingEntityDto mapping = mockExistingMapping();

		Date now = new Date();
		Date accessExpiry = new Date(now.getTime() + 3600 * 1000);
		Date refreshExpiry = new Date(now.getTime() + 86400 * 1000);

		Token accessToken = Token.of(TokenType.ACCESS, "access-token-value", userId.toString(), now,
			accessExpiry);
		Token refreshToken = Token.of(TokenType.REFRESH, "refresh-token-value", userId.toString(),
			now, refreshExpiry);

		UserEntityDto mockUser = UserEntityDto.create(UUID.randomUUID(), "test", "test", "test");

		when(kakaoOAuthService.getUserInfo(code)).thenReturn(userInfo);
		when(oAuthRepository.findBySocialUserIdAndProvider(anyString(), eq(provider)))
			.thenReturn(Optional.of(mapping));
		doNothing().when(oAuthRepository).updateToken(any(), any(OAuthTokenDto.class));

		when(jwtFactory.createAccessToken(userId)).thenReturn(accessToken);
		when(jwtFactory.createRefreshToken(userId)).thenReturn(refreshToken);
		when(userRepository.getById(any())).thenReturn(mockUser);

		// When
		URI result = oAuthLoginService.handleOAuthCallback(provider.name(), code, deviceType.name());

		// Then
		assertThat(result.toString()).contains("needRegister=false");
		assertThat(result.toString()).contains("accessToken=access-token-value");
		assertThat(result.toString()).contains("refreshToken=refresh-token-value");
		verify(tokenRepository).saveToken(eq(userId), eq(deviceType.name()), any(TokenDTO.class));
	}

	@Test
	void handleOAuthCallback_WhenNewUser_ThenRedirectWithSignupInfo() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		DeviceType deviceType = DeviceType.APP;

		OAuthUserInfo userInfo = mockOAuthUserInfo();
		OAuthMappingEntityDto temporaryMapping = mockTemporaryMapping();

		when(kakaoOAuthService.getUserInfo(code)).thenReturn(userInfo);
		when(oAuthRepository.findBySocialUserIdAndProvider(anyString(), eq(provider)))
			.thenReturn(Optional.empty());
		when(oAuthRepository.save(any(OAuthMappingEntityDto.class))).thenReturn(temporaryMapping);

		// When
		URI result = oAuthLoginService.handleOAuthCallback(provider.name(), code, deviceType.name());

		// Then
		assertThat(result.toString()).contains("needRegister=true");
		assertThat(result.toString()).contains("provider=kakao");
		assertThat(result.toString()).contains("deviceType=app");
		assertThat(result.toString()).startsWith("myapp://oauth/callback");
	}

	@Test
	void linkOAuthWithUser_ShouldGenerateTokens() {
		// Given
		User user = UserFactory.create(userId, "test@example.com", "testuser",
			UserStatus.ACTIVE.name());
		DeviceType deviceType = DeviceType.WEB;
		OAuthProvider provider = OAuthProvider.KAKAO;
		OAuthMappingEntityDto mapping = mockTemporaryMapping();
		UserEntityDto userEntityDto = UserEntityDto.create(userId, "test@example.com", "testuser",
			UserStatus.ACTIVE.name());

		Date now = new Date();
		Date accessExpiry = new Date(now.getTime() + 3600 * 1000);
		Date refreshExpiry = new Date(now.getTime() + 86400 * 1000);

		Token accessToken = Token.of(TokenType.ACCESS, "access-token-value", userId.toString(), now,
			accessExpiry);
		Token refreshToken = Token.of(TokenType.REFRESH, "refresh-token-value", userId.toString(),
			now, refreshExpiry);

		when(oAuthRepository.findByOauthMappingEntityIdAndProvider(oAuthMappingEntityId, provider))
			.thenReturn(Optional.of(mapping));
		doNothing().when(oAuthRepository).update(any(OAuthMappingEntityDto.class));
		when(userRepository.getById(userId)).thenReturn(userEntityDto);
		when(jwtFactory.createAccessToken(userId)).thenReturn(accessToken);
		when(jwtFactory.createRefreshToken(userId)).thenReturn(refreshToken);

		// When
		JwtResponse response = oAuthLoginService.linkOAuthWithUser(user,
			oAuthMappingEntityId.toString(), provider, deviceType);

		// Then
		assertThat(response.accessToken()).isEqualTo("access-token-value");
		assertThat(response.refreshToken()).isEqualTo("refresh-token-value");
		verify(tokenRepository).saveToken(eq(userId), eq(deviceType.name()), any(TokenDTO.class));
	}

	@Test
	void linkOAuthWithUser_WithInvalidMapping_ShouldThrowException() {
		// Given
		User user = UserFactory.create(userId, "test@example.com", "testuser",
			UserStatus.ACTIVE.name());
		DeviceType deviceType = DeviceType.WEB;
		OAuthProvider provider = OAuthProvider.KAKAO;

		when(oAuthRepository.findByOauthMappingEntityIdAndProvider(any(UUID.class), eq(provider)))
			.thenReturn(Optional.empty());

		// When, Then
		assertThatExceptionOfType(OAuthNotFoundException.class)
			.isThrownBy(
				() -> oAuthLoginService.linkOAuthWithUser(user, oAuthMappingEntityId.toString(),
					provider, deviceType))
			.withMessage("OAuth 정보를 찾을 수 없습니다.");
	}

	@Test
	void handleOAuthCallback_WithNullState_ShouldDefaultToWebDeviceType() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		DeviceType deviceType = DeviceType.WEB;

		OAuthUserInfo userInfo = mockOAuthUserInfo();
		OAuthMappingEntityDto mapping = mockExistingMapping();

		Date now = new Date();
		Token accessToken = Token.of(TokenType.ACCESS, "access-token", userId.toString(), now,
			new Date(now.getTime() + 3600000));
		Token refreshToken = Token.of(TokenType.REFRESH, "refresh-token", userId.toString(), now,
			new Date(now.getTime() + 86400000));

		UserEntityDto mockUser = UserEntityDto.create(UUID.randomUUID(), "test", "test", "test");

		when(kakaoOAuthService.getUserInfo(code)).thenReturn(userInfo);
		when(oAuthRepository.findBySocialUserIdAndProvider(anyString(), eq(provider)))
			.thenReturn(Optional.of(mapping));
		when(jwtFactory.createAccessToken(userId)).thenReturn(accessToken);
		when(jwtFactory.createRefreshToken(userId)).thenReturn(refreshToken);
		when(userRepository.getById(any())).thenReturn(mockUser);

		// When
		URI result = oAuthLoginService.handleOAuthCallback(provider.name(), code, deviceType.name());

		// Then
		assertThat(result.toString()).contains("needRegister=false");
		assertThat(result.toString()).contains("accessToken=access-token");
		verify(tokenRepository).saveToken(eq(userId), eq(deviceType.name()), any(TokenDTO.class));
	}

	@Test
	void generateTokensForUser_WhenUserNotFound_ShouldThrowException() {
		// Given
		DeviceType deviceType = DeviceType.WEB;
		when(userRepository.getById(userId)).thenThrow(new NoUserException("User not found"));

		// When, Then
		assertThatExceptionOfType(NoUserException.class)
			.isThrownBy(() -> {
				ReflectionTestUtils.invokeMethod(oAuthLoginService, "generateTokensForUser", userId,
					deviceType);
			})
			.withMessage("User not found");
	}

	@Test
	void processOAuthLogin_WhenExistingUserWithTemporaryStatus_ShouldReturnSignupResponse() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		DeviceType deviceType = DeviceType.WEB;

		OAuthUserInfo userInfo = mockOAuthUserInfo();

		UUID temporaryUserId = UUID.randomUUID();
		OAuthMappingEntityDto mappingWithUserIdButTemporary = OAuthMappingEntityDto.create(
			oAuthMappingEntityId,
			"social-user-123",
			"test@example.com",
			"Test User",
			OAuthProvider.KAKAO,
			temporaryUserId,
			userInfo.getOAuthToken(),
			true
		);

		when(kakaoOAuthService.getUserInfo(code)).thenReturn(userInfo);
		when(oAuthRepository.findBySocialUserIdAndProvider(anyString(), eq(provider)))
			.thenReturn(Optional.of(mappingWithUserIdButTemporary));
		doNothing().when(oAuthRepository).updateToken(any(), any(OAuthTokenDto.class));

		// When
		OAuthLoginResponse response = ReflectionTestUtils.invokeMethod(
			oAuthLoginService, "processOAuthLogin", provider, code, deviceType);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.needRegister()).isTrue();
		assertThat(response.OAuthMappingEntityId()).isEqualTo(oAuthMappingEntityId);
	}

	@Test
	void isAppDevice_ShouldReturnTrueForAppDeviceType() {
		// When
		boolean result = ReflectionTestUtils.invokeMethod(oAuthLoginService, "isAppDevice",
			DeviceType.APP);

		// Then
		assertThat(result).isTrue();
	}

	@Test
	void isAppDevice_ShouldReturnFalseForWebDeviceType() {
		// When
		boolean result = ReflectionTestUtils.invokeMethod(oAuthLoginService, "isAppDevice",
			DeviceType.WEB);

		// Then
		assertThat(result).isFalse();
	}

	@Test
	void getRedirectUriByDeviceType_ShouldReturnAppRedirectForAppDeviceType() {
		// When
		String result = ReflectionTestUtils.invokeMethod(oAuthLoginService,
			"getRedirectUriByDeviceType", DeviceType.APP);

		// Then
		assertThat(result).isEqualTo("myapp://oauth/callback");
	}

	@Test
	void getRedirectUriByDeviceType_ShouldReturnWebRedirectForWebDeviceType() {
		// When
		String result = ReflectionTestUtils.invokeMethod(oAuthLoginService,
			"getRedirectUriByDeviceType", DeviceType.WEB);

		// Then
		assertThat(result).isEqualTo("http://test/oauth/callback");
	}

	@Test
	void handleOAuthCallback_ShouldHandleWebExistingUserCorrectly() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		DeviceType deviceType = DeviceType.WEB;

		OAuthUserInfo userInfo = mockOAuthUserInfo();
		OAuthMappingEntityDto mapping = mockExistingMapping();

		Date now = new Date();
		Token accessToken = Token.of(TokenType.ACCESS, "access-token", userId.toString(), now,
			new Date(now.getTime() + 3600000));
		Token refreshToken = Token.of(TokenType.REFRESH, "refresh-token", userId.toString(), now,
			new Date(now.getTime() + 86400000));

		UserEntityDto mockUser = UserEntityDto.create(UUID.randomUUID(), "test", "test", "test");

		when(kakaoOAuthService.getUserInfo(code)).thenReturn(userInfo);
		when(oAuthRepository.findBySocialUserIdAndProvider(anyString(), eq(provider)))
			.thenReturn(Optional.of(mapping));
		when(jwtFactory.createAccessToken(userId)).thenReturn(accessToken);
		when(jwtFactory.createRefreshToken(userId)).thenReturn(refreshToken);
		when(userRepository.getById(any())).thenReturn(mockUser);

		// When
		URI result = oAuthLoginService.handleOAuthCallback(provider.name(), code, deviceType.name());

		// Then
		assertThat(result.toString()).contains("#needRegister=false");
		assertThat(result.toString()).contains("http://test/oauth/callback#");
		assertThat(result.getFragment()).contains("accessToken=access-token");
	}

	@Test
	void processOAuthLogin_WhenUserIdIsNullButNotTemporary_ShouldHandleGracefully() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		DeviceType deviceType = DeviceType.WEB;

		OAuthUserInfo userInfo = mockOAuthUserInfo();

		OAuthMappingEntityDto unusualMapping = OAuthMappingEntityDto.create(
			oAuthMappingEntityId,
			"social-user-123",
			"test@example.com",
			"Test User",
			OAuthProvider.KAKAO,
			null,
			userInfo.getOAuthToken(),
			false
		);

		when(kakaoOAuthService.getUserInfo(code)).thenReturn(userInfo);
		when(oAuthRepository.findBySocialUserIdAndProvider(anyString(), eq(provider)))
			.thenReturn(Optional.of(unusualMapping));
		doNothing().when(oAuthRepository).updateToken(any(), any(OAuthTokenDto.class));

		// When
		OAuthLoginResponse response = ReflectionTestUtils.invokeMethod(
			oAuthLoginService, "processOAuthLogin", provider, code, deviceType);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.needRegister()).isTrue();
		assertThat(response.OAuthMappingEntityId()).isEqualTo(oAuthMappingEntityId);
	}

	@Test
	void linkOAuthWithUser_WithInvalidUUID_ShouldThrowException() {
		// Given
		User user = UserFactory.create(userId, "test@example.com", "testuser",
			UserStatus.ACTIVE.name());
		String invalidUUID = "not-a-uuid";

		// When, Then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> {
				oAuthLoginService.linkOAuthWithUser(user, invalidUUID, OAuthProvider.KAKAO,
					DeviceType.WEB);
			});
	}

	@Test
	void getOAuthService_WhenUnsupportedProvider_ShouldThrowException() {
		// Given
		OAuthProvider provider = OAuthProvider.UNSUPPORTED;

		// When, Then
		assertThatExceptionOfType(OAuthBadRequestException.class)
			.isThrownBy(() -> oAuthLoginService.getOAuthLoginUrl(provider.name(), DeviceType.WEB.name()))
			.withMessage("지원하지 않는 OAuth 제공자입니다: " + provider);
	}

	@Test
	void handleOAuthCallback_WhenNewUserAndErrorSavingMapping_ShouldThrowException() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		DeviceType deviceType = DeviceType.APP;
		OAuthUserInfo userInfo = mockOAuthUserInfo();

		when(kakaoOAuthService.getUserInfo(code)).thenReturn(userInfo);
		when(oAuthRepository.findBySocialUserIdAndProvider(anyString(), eq(provider)))
			.thenReturn(Optional.empty());
		when(oAuthRepository.save(any(OAuthMappingEntityDto.class)))
			.thenThrow(new RuntimeException("Database error"));

		// When, Then
		assertThatExceptionOfType(RuntimeException.class)
			.isThrownBy(() -> oAuthLoginService.handleOAuthCallback(provider.name(), code, deviceType.name()))
			.withMessage("Database error");
	}

	@Test
	void processOAuthLogin_WhenOAuthServiceThrowsException_ShouldPropagateException() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		DeviceType deviceType = DeviceType.WEB;

		when(kakaoOAuthService.getUserInfo(code))
			.thenThrow(new OAuthBadRequestException("API error"));

		// When, Then
		assertThatExceptionOfType(OAuthBadRequestException.class)
			.isThrownBy(() -> oAuthLoginService.processOAuthLogin(provider, code, deviceType))
			.withMessage("API error");
	}

	@Test
	void linkOAuthWithUser_WhenUpdateFails_ShouldPropagateException() {
		// Given
		User user = UserFactory.create(userId, "test@example.com", "testuser",
			UserStatus.ACTIVE.name());
		DeviceType deviceType = DeviceType.WEB;
		OAuthProvider provider = OAuthProvider.KAKAO;
		OAuthMappingEntityDto mapping = mockTemporaryMapping();

		when(oAuthRepository.findByOauthMappingEntityIdAndProvider(oAuthMappingEntityId, provider))
			.thenReturn(Optional.of(mapping));
		doThrow(new RuntimeException("Update failed"))
			.when(oAuthRepository).update(any(OAuthMappingEntityDto.class));

		// When, Then
		assertThatExceptionOfType(RuntimeException.class)
			.isThrownBy(() ->
				oAuthLoginService.linkOAuthWithUser(user, oAuthMappingEntityId.toString(), provider,
					deviceType))
			.withMessage("Update failed");
	}

	@Test
	void generateTokensForUser_WhenTokenCreationFails_ShouldPropagateException() {
		// Given
		DeviceType deviceType = DeviceType.WEB;
		UserEntityDto userEntityDto = UserEntityDto.create(userId, "test@example.com", "testuser",
			UserStatus.ACTIVE.name());

		when(userRepository.getById(userId)).thenReturn(userEntityDto);
		when(jwtFactory.createAccessToken(userId)).thenThrow(
			new RuntimeException("Token creation failed"));

		// When, Then
		assertThatExceptionOfType(RuntimeException.class)
			.isThrownBy(() -> {
				ReflectionTestUtils.invokeMethod(oAuthLoginService, "generateTokensForUser", userId,
					deviceType);
			})
			.withMessage("Token creation failed");
	}

	private OAuthUserInfo mockOAuthUserInfo() {
		OAuthToken oauthToken = OAuthToken.ofKakao(
			"fake-token", "fake-token", 10000L);

		return OAuthUserInfo.create("social-user-123", "test@example.com", "Test User",
			OAuthProvider.KAKAO, oauthToken);
	}

	private OAuthMappingEntityDto mockExistingMapping() {
		OAuthToken oauthToken = OAuthToken.ofKakao(
			"fake-token", "fake-token", 10000L);

		return OAuthMappingEntityDto.create(
			oAuthMappingEntityId,
			"social-user-123",
			"test@example.com",
			"Test User",
			OAuthProvider.KAKAO,
			userId,
			oauthToken,
			false
		);
	}

	private OAuthMappingEntityDto mockTemporaryMapping() {
		OAuthToken oauthToken = OAuthToken.ofKakao(
			"fake-token", "fake-token", 10000L);

		OAuthMapping oAuthMapping = OAuthMapping.createTemporary("social-user-123",
			"test@example.com",
			"Test User",
			OAuthProvider.KAKAO,
			oauthToken);

		return oAuthMapping.toDto();
	}
}
