package com.newzet.api.auth.business.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.time.LocalDateTime;
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
import com.newzet.api.auth.business.dto.TokenDTO;
import com.newzet.api.auth.business.service.oauth.OAuthRepository;
import com.newzet.api.auth.business.service.oauth.OAuthService;
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
		String state = "mobile";
		when(kakaoOAuthService.isBackendRedirect()).thenReturn(true);
		when(kakaoOAuthService.getRedirectUrl(state)).thenReturn(
			"https://kauth.kakao.com/oauth/authorize?...");

		// When
		String url = oAuthLoginService.getOAuthLoginUrl(provider, state);

		// Then
		assertThat(url).isEqualTo("https://kauth.kakao.com/oauth/authorize?...");
		verify(kakaoOAuthService).isBackendRedirect();
		verify(kakaoOAuthService).getRedirectUrl(state);
	}

	@Test
	void getOAuthLoginUrl_WhenProviderDoesNotSupportBackendRedirect_ThenThrowsException() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String state = "web";
		when(kakaoOAuthService.isBackendRedirect()).thenReturn(false);

		// When, Then
		assertThatExceptionOfType(OAuthBadRequestException.class)
			.isThrownBy(() -> oAuthLoginService.getOAuthLoginUrl(provider, state))
			.withMessage("이 제공자는 백엔드 리다이렉트를 지원하지 않습니다.");
	}

	@Test
	void handleOAuthCallback_WhenExistingUser_ThenRedirectWithTokens() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		String state = "web";

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
		doNothing().when(oAuthRepository).update(any(OAuthMappingEntityDto.class));

		when(jwtFactory.createAccessToken(userId)).thenReturn(accessToken);
		when(jwtFactory.createRefreshToken(userId)).thenReturn(refreshToken);
		when(userRepository.getById(any())).thenReturn(mockUser);

		// When
		URI result = oAuthLoginService.handleOAuthCallback(provider, code, state);

		// Then
		assertThat(result.toString()).contains("needRegister=false");
		assertThat(result.toString()).contains("accessToken=access-token-value");
		assertThat(result.toString()).contains("refreshToken=refresh-token-value");
		verify(tokenRepository).saveToken(eq(userId), eq(state), any(TokenDTO.class));
	}

	@Test
	void handleOAuthCallback_WhenNewUser_ThenRedirectWithSignupInfo() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		String state = "mobile";

		OAuthUserInfo userInfo = mockOAuthUserInfo();
		OAuthMappingEntityDto temporaryMapping = mockTemporaryMapping();

		when(kakaoOAuthService.getUserInfo(code)).thenReturn(userInfo);
		when(oAuthRepository.findBySocialUserIdAndProvider(anyString(), eq(provider)))
			.thenReturn(Optional.empty());
		when(oAuthRepository.save(any(OAuthMappingEntityDto.class))).thenReturn(temporaryMapping);

		// When
		URI result = oAuthLoginService.handleOAuthCallback(provider, code, state);

		// Then
		assertThat(result.toString()).contains("needRegister=true");
		assertThat(result.toString()).contains("provider=kakao");
		assertThat(result.toString()).startsWith("myapp://oauth/callback");
	}

	@Test
	void linkOAuthWithUser_ShouldGenerateTokens() {
		// Given
		User user = UserFactory.create(userId, "test@example.com", "testuser",
			UserStatus.ACTIVE.name());
		String deviceType = "web";
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
		verify(tokenRepository).saveToken(eq(userId), eq(deviceType), any(TokenDTO.class));
	}

	@Test
	void linkOAuthWithUser_WithInvalidMapping_ShouldThrowException() {
		// Given
		User user = UserFactory.create(userId, "test@example.com", "testuser",
			UserStatus.ACTIVE.name());
		String deviceType = "web";
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
	void getOAuthLoginUrl_WithUnsupportedProvider_ShouldThrowException() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> OAuthProvider.valueOf("ERROR"));
	}

	@Test
	void handleOAuthCallback_WithNullState_ShouldUseDefaultDeviceType() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		String state = null;

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
		URI result = oAuthLoginService.handleOAuthCallback(provider, code, state);

		// Then
		assertThat(result.toString()).contains("needRegister=false");
		assertThat(result.toString()).contains("accessToken=access-token");
		verify(tokenRepository).saveToken(eq(userId), eq("web"), any(TokenDTO.class));
	}

	@Test
	void generateTokensForUser_WhenUserNotFound_ShouldThrowException() {
		// Given
		String deviceType = "web";
		when(userRepository.getById(userId)).thenThrow(new NoUserException("User not found"));

		// When, Then
		assertThatExceptionOfType(OAuthNotFoundException.class)
			.isThrownBy(() -> {
				ReflectionTestUtils.invokeMethod(oAuthLoginService, "generateTokensForUser", userId,
					deviceType);
			})
			.withMessage("연결된 사용자 계정을 찾을 수 없습니다.");
	}

	@Test
	void processOAuthLogin_WhenExistingUserWithTemporaryStatus_ShouldReturnSignupResponse() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		String deviceType = "web";

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
		doNothing().when(oAuthRepository).update(any(OAuthMappingEntityDto.class));

		// When
		OAuthLoginResponse response = ReflectionTestUtils.invokeMethod(
			oAuthLoginService, "processOAuthLogin", provider, code, deviceType);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.needRegister()).isTrue();
		assertThat(response.OAuthMappingEntityId()).isEqualTo(oAuthMappingEntityId);
	}

	@Test
	void isAppDevice_ShouldReturnTrueForMobile() {
		// When, Then
		boolean result = ReflectionTestUtils.invokeMethod(oAuthLoginService, "isAppDevice",
			"mobile");
		assertThat(result).isTrue();
	}

	@Test
	void isAppDevice_ShouldReturnTrueForApp() {
		// When, Then
		boolean result = ReflectionTestUtils.invokeMethod(oAuthLoginService, "isAppDevice", "app");
		assertThat(result).isTrue();
	}

	@Test
	void isAppDevice_ShouldReturnFalseForWebOrOther() {
		// When, Then
		boolean resultWeb = ReflectionTestUtils.invokeMethod(oAuthLoginService, "isAppDevice",
			"web");
		boolean resultOther = ReflectionTestUtils.invokeMethod(oAuthLoginService, "isAppDevice",
			"other");

		assertThat(resultWeb).isFalse();
		assertThat(resultOther).isFalse();
	}

	@Test
	void getRedirectUriByDeviceType_ShouldReturnAppRedirectForAppDevices() {
		// When, Then
		String resultMobile = ReflectionTestUtils.invokeMethod(oAuthLoginService,
			"getRedirectUriByDeviceType", "mobile");
		String resultApp = ReflectionTestUtils.invokeMethod(oAuthLoginService,
			"getRedirectUriByDeviceType", "app");

		assertThat(resultMobile).isEqualTo("myapp://oauth/callback");
		assertThat(resultApp).isEqualTo("myapp://oauth/callback");
	}

	@Test
	void getRedirectUriByDeviceType_ShouldReturnWebRedirectForNonAppDevices() {
		// When, Then
		String resultWeb = ReflectionTestUtils.invokeMethod(oAuthLoginService,
			"getRedirectUriByDeviceType", "web");
		String resultOther = ReflectionTestUtils.invokeMethod(oAuthLoginService,
			"getRedirectUriByDeviceType", "other");

		assertThat(resultWeb).isEqualTo("http://test/oauth/callback");
		assertThat(resultOther).isEqualTo("http://test/oauth/callback");
	}

	@Test
	void getOAuthService_ShouldThrowExceptionForUnsupportedProvider() {
		// Given
		OAuthProvider provider = OAuthProvider.UNSUPPORTED;

		// When, Then
		assertThatExceptionOfType(OAuthBadRequestException.class)
			.isThrownBy(() -> {
				ReflectionTestUtils.invokeMethod(oAuthLoginService, "getOAuthService", provider);
			})
			.withMessage("지원하지 않는 OAuth 제공자입니다: " + provider);
	}

	@Test
	void handleOAuthCallback_ShouldHandleWebExistingUserCorrectly() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		String state = "web";

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
		URI result = oAuthLoginService.handleOAuthCallback(provider, code, state);

		// Then
		assertThat(result.toString()).contains("#needRegister=false");
		assertThat(result.toString()).contains("http://test/oauth/callback#");
		assertThat(result.getFragment()).contains("accessToken=access-token");
	}

	@Test
	void handleOAuthCallback_ShouldHandleWebNewUserCorrectly() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		String state = "web";

		OAuthUserInfo userInfo = mockOAuthUserInfo();
		OAuthMappingEntityDto temporaryMapping = mockTemporaryMapping();

		when(kakaoOAuthService.getUserInfo(code)).thenReturn(userInfo);
		when(oAuthRepository.findBySocialUserIdAndProvider(anyString(), eq(provider)))
			.thenReturn(Optional.empty());
		when(oAuthRepository.save(any(OAuthMappingEntityDto.class))).thenReturn(temporaryMapping);

		// When
		URI result = oAuthLoginService.handleOAuthCallback(provider, code, state);

		// Then
		assertThat(result.toString()).contains("#needRegister=true");
		assertThat(result.toString()).contains("http://test/oauth/callback#");
		assertThat(result.getFragment()).contains("provider=kakao");
	}

	@Test
	void extractDeviceTypeFromState_ShouldHandleEmptyString() {
		// Given
		String emptyState = "";

		// When
		String result = ReflectionTestUtils.invokeMethod(oAuthLoginService,
			"extractDeviceTypeFromState", emptyState);

		// Then
		assertThat(result).isEqualTo("web");
	}

	@Test
	void getOAuthService_ShouldThrowExceptionWhenServiceIsWrongType() {
		// Given
		Map<String, Object> wrongTypedServices = new HashMap<>();
		wrongTypedServices.put("kakaoOAuthService", "Not a service but a string");
		ReflectionTestUtils.setField(oAuthLoginService, "oAuthServices", wrongTypedServices);

		// When, Then
		assertThatExceptionOfType(ClassCastException.class)
			.isThrownBy(() -> {
				ReflectionTestUtils.invokeMethod(oAuthLoginService, "getOAuthService",
					OAuthProvider.KAKAO);
			});

		Map<String, OAuthService> originalServices = new HashMap<>();
		originalServices.put("kakaoOAuthService", kakaoOAuthService);
		originalServices.put("googleOAuthService", googleOAuthService);
		ReflectionTestUtils.setField(oAuthLoginService, "oAuthServices", originalServices);
	}

	@Test
	void handleOAuthCallback_WithExtremelyLongState_ShouldNotThrowException() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		String veryLongState = "a".repeat(1000);

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
		URI result = oAuthLoginService.handleOAuthCallback(provider, code, veryLongState);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.toString()).contains("accessToken=access-token");
	}

	@Test
	void extractDeviceTypeFromState_WithNullState_ShouldReturnDefaultDeviceType() {
		// When
		String result = ReflectionTestUtils.invokeMethod(oAuthLoginService,
			"extractDeviceTypeFromState", (String)null);

		// Then
		assertThat(result).isEqualTo("web");
	}

	@Test
	void isAppDevice_WithNullDeviceType_ShouldNotThrowException() {
		// When
		boolean result = ReflectionTestUtils.invokeMethod(oAuthLoginService, "isAppDevice",
			(String)null);

		// Then
		assertThat(result).isFalse();
	}

	@Test
	void getRedirectUriByDeviceType_WithNullDeviceType_ShouldReturnWebRedirect() {
		// When
		String result = ReflectionTestUtils.invokeMethod(oAuthLoginService,
			"getRedirectUriByDeviceType", (String)null);

		// Then`
		assertThat(result).isEqualTo("http://test/oauth/callback");
	}

	@Test
	void processOAuthLogin_WhenUserIdIsNullButNotTemporary_ShouldHandleGracefully() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String code = "test-code";
		String deviceType = "web";

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
		doNothing().when(oAuthRepository).update(any(OAuthMappingEntityDto.class));

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
				oAuthLoginService.linkOAuthWithUser(user, invalidUUID, OAuthProvider.KAKAO, "web");
			});
	}

	private OAuthUserInfo mockOAuthUserInfo() {
		OAuthToken oauthToken = OAuthToken.builder()
			.provider(OAuthProvider.KAKAO)
			.accessToken("fake-token")
			.refreshToken("fake-token")
			.tokenPrefix("Bearer")
			.expiresIn(10000L)
			.scope("test")
			.issuedAt(LocalDateTime.now())
			.build();
		return OAuthUserInfo.create("social-user-123", "test@example.com", "Test User",
			OAuthProvider.KAKAO, oauthToken);
	}

	private OAuthMappingEntityDto mockExistingMapping() {
		OAuthToken oauthToken = OAuthToken.builder()
			.provider(OAuthProvider.KAKAO)
			.accessToken("fake-token")
			.refreshToken("fake-token")
			.tokenPrefix("Bearer")
			.expiresIn(10000L)
			.scope("test")
			.issuedAt(LocalDateTime.now())
			.build();
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
		OAuthToken oauthToken = OAuthToken.builder()
			.provider(OAuthProvider.KAKAO)
			.accessToken("fake-token")
			.refreshToken("fake-token")
			.tokenPrefix("Bearer")
			.expiresIn(10000L)
			.scope("test")
			.issuedAt(LocalDateTime.now())
			.build();
		return OAuthMappingEntityDto.createTemporary(
			"social-user-123",
			"test@example.com",
			"Test User",
			OAuthProvider.KAKAO,
			oauthToken
		);
	}
}
