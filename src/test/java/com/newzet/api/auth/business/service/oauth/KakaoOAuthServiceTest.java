package com.newzet.api.auth.business.service.oauth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.newzet.api.auth.domain.OAuthUserInfo;
import com.newzet.api.auth.exception.OAuthErrorException;

@ExtendWith(MockitoExtension.class)
class KakaoOAuthServiceTest {

	private final String FAKE_CLIENT_ID = "fake-client-id";
	private final String FAKE_REDIRECT_URI = "http://test/oauth/callback";
	@Mock
	private RestTemplate restTemplate;
	@InjectMocks
	private KakaoOAuthService kakaoOAuthService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(kakaoOAuthService, "clientId", FAKE_CLIENT_ID);
		ReflectionTestUtils.setField(kakaoOAuthService, "redirectUri", FAKE_REDIRECT_URI);
	}

	@Test
	void isBackendRedirect_ShouldReturnTrue() {
		assertThat(kakaoOAuthService.isBackendRedirect()).isTrue();
	}

	@Test
	void getRedirectUrl_ShouldReturnCorrectUrl() {
		// Given
		String state = "test-state";

		// When
		String redirectUrl = kakaoOAuthService.getRedirectUrl(state);

		// Then
		assertThat(redirectUrl).contains("kauth.kakao.com/oauth/authorize");
		assertThat(redirectUrl).contains("client_id=" + FAKE_CLIENT_ID);
		assertThat(redirectUrl).contains("redirect_uri=" + FAKE_REDIRECT_URI);
		assertThat(redirectUrl).contains("state=" + state);
	}

	@Test
	void getRedirectUrl_WithNullState_ShouldReturnValidUrl() {
		// When
		String redirectUrl = kakaoOAuthService.getRedirectUrl(null);

		// Then
		assertThat(redirectUrl).contains("kauth.kakao.com/oauth/authorize");
		assertThat(redirectUrl).doesNotContain("state=");
	}

	@Test
	void getUserInfo_ShouldRetrieveTokenAndUserInfo() {
		// Given
		String code = "test-code";

		KakaoTokenResponse tokenResponse = new KakaoTokenResponse(
			"fake-access-token",
			"fake-refresh-token",
			21599L,
			999999L,
			"bearer",
			"profile"
		);

		ResponseEntity<KakaoTokenResponse> tokenResponseEntity =
			new ResponseEntity<>(tokenResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kauth.kakao.com/oauth/token"),
			eq(HttpMethod.POST),
			any(),
			eq(KakaoTokenResponse.class)
		)).thenReturn(tokenResponseEntity);

		KakaoUserInfoResponse.KakaoAccount.Profile profile = new KakaoUserInfoResponse.KakaoAccount.Profile(
			"TestUser");
		KakaoUserInfoResponse.KakaoAccount account = new KakaoUserInfoResponse.KakaoAccount(
			"test@example.com", profile);
		KakaoUserInfoResponse userInfoResponse = new KakaoUserInfoResponse("123456789", account);

		ResponseEntity<KakaoUserInfoResponse> userInfoResponseEntity =
			new ResponseEntity<>(userInfoResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kapi.kakao.com/v2/user/me"),
			eq(HttpMethod.GET),
			any(),
			eq(KakaoUserInfoResponse.class)
		)).thenReturn(userInfoResponseEntity);

		// When
		OAuthUserInfo result = kakaoOAuthService.getUserInfo(code);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getSocialUserId()).isEqualTo("123456789");
		assertThat(result.getEmail()).isEqualTo("test@example.com");
		assertThat(result.getName()).isEqualTo("TestUser");
		assertThat(result.getOAuthToken().getAccessToken()).isEqualTo("fake-access-token");
		assertThat(result.getOAuthToken().getRefreshToken()).isEqualTo("fake-refresh-token");
	}

	@Test
	void getUserInfo_WhenTokenRequestFails_ShouldThrowOAuthException() {
		// Given
		String code = "invalid-code";
		when(restTemplate.exchange(
			contains("kauth.kakao.com/oauth/token"),
			eq(HttpMethod.POST),
			any(),
			eq(KakaoTokenResponse.class)
		)).thenThrow(new RestClientException("API Error"));

		// When, Then
		assertThatExceptionOfType(OAuthErrorException.class)
			.isThrownBy(() -> kakaoOAuthService.getUserInfo(code));
	}

	@Test
	void getUserInfo_WhenUserInfoRequestFails_ShouldThrowOAuthException() {
		// Given
		String code = "test-code";

		KakaoTokenResponse tokenResponse = new KakaoTokenResponse(
			"fake-access-token", null, null, null, "bearer", null);
		ResponseEntity<KakaoTokenResponse> tokenResponseEntity =
			new ResponseEntity<>(tokenResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kauth.kakao.com/oauth/token"),
			eq(HttpMethod.POST),
			any(),
			eq(KakaoTokenResponse.class)
		)).thenReturn(tokenResponseEntity);

		when(restTemplate.exchange(
			contains("kapi.kakao.com/v2/user/me"),
			eq(HttpMethod.GET),
			any(),
			eq(KakaoUserInfoResponse.class)
		)).thenThrow(new RestClientException("API Error"));

		// When, Then
		assertThatExceptionOfType(OAuthErrorException.class)
			.isThrownBy(() -> kakaoOAuthService.getUserInfo(code));
	}

	@Test
	void getUserInfo_WhenUserInfoResponseIsNull_ShouldThrowOAuthException() {
		// Given
		String code = "test-code";

		KakaoTokenResponse tokenResponse = new KakaoTokenResponse(
			"fake-access-token", null, null, null, null, null);
		ResponseEntity<KakaoTokenResponse> tokenResponseEntity =
			new ResponseEntity<>(tokenResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kauth.kakao.com/oauth/token"),
			eq(HttpMethod.POST),
			any(),
			eq(KakaoTokenResponse.class)
		)).thenReturn(tokenResponseEntity);

		ResponseEntity<KakaoUserInfoResponse> userInfoResponseEntity =
			new ResponseEntity<>(null, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kapi.kakao.com/v2/user/me"),
			eq(HttpMethod.GET),
			any(),
			eq(KakaoUserInfoResponse.class)
		)).thenReturn(userInfoResponseEntity);

		// When, Then
		assertThatExceptionOfType(OAuthErrorException.class)
			.isThrownBy(() -> kakaoOAuthService.getUserInfo(code));
	}

	@Test
	void getUserInfo_WithMissingOptionalFields_ShouldHandleGracefully() {
		// Given
		String code = "test-code";

		KakaoTokenResponse tokenResponse = new KakaoTokenResponse(
			"fake-access-token", null, null, null, null, null);
		ResponseEntity<KakaoTokenResponse> tokenResponseEntity =
			new ResponseEntity<>(tokenResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kauth.kakao.com/oauth/token"),
			eq(HttpMethod.POST),
			any(),
			eq(KakaoTokenResponse.class)
		)).thenReturn(tokenResponseEntity);

		KakaoUserInfoResponse userInfoResponse = new KakaoUserInfoResponse("123456789", null);
		ResponseEntity<KakaoUserInfoResponse> userInfoResponseEntity =
			new ResponseEntity<>(userInfoResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kapi.kakao.com/v2/user/me"),
			eq(HttpMethod.GET),
			any(),
			eq(KakaoUserInfoResponse.class)
		)).thenReturn(userInfoResponseEntity);

		// When
		OAuthUserInfo result = kakaoOAuthService.getUserInfo(code);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getSocialUserId()).isEqualTo("123456789");
		assertThat(result.getEmail()).isNull();
		assertThat(result.getName()).isEqualTo("Unknown");
		assertThat(result.getOAuthToken().getAccessToken()).isEqualTo("fake-access-token");
	}

	@Test
	void getUserInfo_WithEmailButNoProfile_ShouldHandleGracefully() {
		// Given
		String code = "test-code";

		KakaoTokenResponse tokenResponse = new KakaoTokenResponse(
			"fake-access-token", null, null, null, null, null);
		ResponseEntity<KakaoTokenResponse> tokenResponseEntity =
			new ResponseEntity<>(tokenResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kauth.kakao.com/oauth/token"),
			eq(HttpMethod.POST),
			any(),
			eq(KakaoTokenResponse.class)
		)).thenReturn(tokenResponseEntity);

		KakaoUserInfoResponse.KakaoAccount account = new KakaoUserInfoResponse.KakaoAccount(
			"test@example.com", null);
		KakaoUserInfoResponse userInfoResponse = new KakaoUserInfoResponse("123456789", account);
		ResponseEntity<KakaoUserInfoResponse> userInfoResponseEntity =
			new ResponseEntity<>(userInfoResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kapi.kakao.com/v2/user/me"),
			eq(HttpMethod.GET),
			any(),
			eq(KakaoUserInfoResponse.class)
		)).thenReturn(userInfoResponseEntity);

		// When
		OAuthUserInfo result = kakaoOAuthService.getUserInfo(code);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getSocialUserId()).isEqualTo("123456789");
		assertThat(result.getEmail()).isEqualTo("test@example.com");
		assertThat(result.getName()).isEqualTo("Unknown");
	}

	@Test
	void getUserInfo_WhenTokenResponseBodyIsNull_ShouldThrowOAuthException() {
		// Given
		String code = "test-code";

		ResponseEntity<KakaoTokenResponse> tokenResponseEntity = new ResponseEntity<>(null,
			HttpStatus.OK);
		when(restTemplate.exchange(
			contains("kauth.kakao.com/oauth/token"),
			eq(HttpMethod.POST),
			any(),
			eq(KakaoTokenResponse.class)
		)).thenReturn(tokenResponseEntity);

		// When, Then
		assertThatExceptionOfType(OAuthErrorException.class)
			.isThrownBy(() -> kakaoOAuthService.getUserInfo(code));
	}

	@Test
	void getUserInfo_WhenUserIdIsNull_ShouldThrowOAuthException() {
		// Given
		String code = "test-code";

		KakaoTokenResponse tokenResponse = new KakaoTokenResponse(
			"fake-access-token", null, null, null, null, null);
		ResponseEntity<KakaoTokenResponse> tokenResponseEntity =
			new ResponseEntity<>(tokenResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kauth.kakao.com/oauth/token"),
			eq(HttpMethod.POST),
			any(),
			eq(KakaoTokenResponse.class)
		)).thenReturn(tokenResponseEntity);

		KakaoUserInfoResponse userInfoResponse = new KakaoUserInfoResponse(null, null);
		ResponseEntity<KakaoUserInfoResponse> userInfoResponseEntity =
			new ResponseEntity<>(userInfoResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kapi.kakao.com/v2/user/me"),
			eq(HttpMethod.GET),
			any(),
			eq(KakaoUserInfoResponse.class)
		)).thenReturn(userInfoResponseEntity);

		// When, Then
		assertThatExceptionOfType(OAuthErrorException.class)
			.isThrownBy(() -> kakaoOAuthService.getUserInfo(code));
	}

	@Test
	void getUserInfo_WithNullEmail_ShouldHandleGracefully() {
		// Given
		String code = "test-code";

		KakaoTokenResponse tokenResponse = new KakaoTokenResponse(
			"fake-access-token", null, null, null, null, null);
		ResponseEntity<KakaoTokenResponse> tokenResponseEntity =
			new ResponseEntity<>(tokenResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kauth.kakao.com/oauth/token"),
			eq(HttpMethod.POST),
			any(),
			eq(KakaoTokenResponse.class)
		)).thenReturn(tokenResponseEntity);

		KakaoUserInfoResponse.KakaoAccount account = new KakaoUserInfoResponse.KakaoAccount(null,
			null);
		KakaoUserInfoResponse userInfoResponse = new KakaoUserInfoResponse("123456789", account);
		ResponseEntity<KakaoUserInfoResponse> userInfoResponseEntity =
			new ResponseEntity<>(userInfoResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kapi.kakao.com/v2/user/me"),
			eq(HttpMethod.GET),
			any(),
			eq(KakaoUserInfoResponse.class)
		)).thenReturn(userInfoResponseEntity);

		// When
		OAuthUserInfo result = kakaoOAuthService.getUserInfo(code);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getSocialUserId()).isEqualTo("123456789");
		assertThat(result.getEmail()).isNull();
		assertThat(result.getName()).isEqualTo("Unknown");
	}

	@Test
	void getUserInfo_WithNullAccessToken_ShouldThrowOAuthException() {
		// Given
		String code = "test-code";

		KakaoTokenResponse tokenResponse = new KakaoTokenResponse(
			null, null, null, null, null, null);
		ResponseEntity<KakaoTokenResponse> tokenResponseEntity =
			new ResponseEntity<>(tokenResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kauth.kakao.com/oauth/token"),
			eq(HttpMethod.POST),
			any(),
			eq(KakaoTokenResponse.class)
		)).thenReturn(tokenResponseEntity);

		// When, Then
		assertThatExceptionOfType(OAuthErrorException.class)
			.isThrownBy(() -> kakaoOAuthService.getUserInfo(code));
	}

	@Test
	void getUserInfo_WithProfileButNullNickname_ShouldHandleGracefully() {
		// Given
		String code = "test-code";

		KakaoTokenResponse tokenResponse = new KakaoTokenResponse(
			"fake-access-token", null, null, null, null, null);
		ResponseEntity<KakaoTokenResponse> tokenResponseEntity =
			new ResponseEntity<>(tokenResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kauth.kakao.com/oauth/token"),
			eq(HttpMethod.POST),
			any(),
			eq(KakaoTokenResponse.class)
		)).thenReturn(tokenResponseEntity);

		KakaoUserInfoResponse.KakaoAccount.Profile profile = new KakaoUserInfoResponse.KakaoAccount.Profile(
			null);
		KakaoUserInfoResponse.KakaoAccount account = new KakaoUserInfoResponse.KakaoAccount(
			"test@example.com", profile);
		KakaoUserInfoResponse userInfoResponse = new KakaoUserInfoResponse("123456789", account);
		ResponseEntity<KakaoUserInfoResponse> userInfoResponseEntity =
			new ResponseEntity<>(userInfoResponse, HttpStatus.OK);

		when(restTemplate.exchange(
			contains("kapi.kakao.com/v2/user/me"),
			eq(HttpMethod.GET),
			any(),
			eq(KakaoUserInfoResponse.class)
		)).thenReturn(userInfoResponseEntity);

		// When
		OAuthUserInfo result = kakaoOAuthService.getUserInfo(code);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getSocialUserId()).isEqualTo("123456789");
		assertThat(result.getEmail()).isEqualTo("test@example.com");
		assertThat(result.getName()).isEqualTo("Unknown");
	}
}
