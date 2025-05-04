package com.newzet.api.auth.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.newzet.api.auth.exception.OAuthErrorException;

class OAuthTokenTest {

	@Test
	void ofKakao_WhenValidParameters_ThenCreateOAuthToken() {
		// Given
		String accessToken = "valid-access-token";
		String refreshToken = "valid-refresh-token";
		Long expiresIn = 3600L;

		// When
		OAuthToken token = OAuthToken.ofKakao(accessToken, refreshToken, expiresIn);

		// Then
		assertThat(token).isNotNull();
		assertThat(token.getProvider()).isEqualTo(OAuthProvider.KAKAO);
		assertThat(token.getAccessToken()).isEqualTo(accessToken);
		assertThat(token.getRefreshToken()).isEqualTo(refreshToken);
		assertThat(token.getExpiresIn()).isEqualTo(expiresIn);
		assertThat(token.getIssuedAt()).isNotNull();
	}

	@Test
	void ofKakao_WhenAccessTokenIsNull_ThenThrowOAuthErrorException() {
		// Given
		String accessToken = null;
		String refreshToken = "valid-refresh-token";
		Long expiresIn = 3600L;

		// When, Then
		assertThatThrownBy(
			() -> OAuthToken.ofKakao(accessToken, refreshToken, expiresIn))
			.isInstanceOf(OAuthErrorException.class)
			.hasMessage("응답이 올바르지 않아 accessToken이 전달되지 않았습니다.");
	}

	@Test
	void create_WhenValidParameters_ThenCreateOAuthToken() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String accessToken = "valid-access-token";
		String refreshToken = "valid-refresh-token";
		Long expiresIn = 3600L;

		// When
		OAuthToken token = OAuthToken.create(provider, accessToken, refreshToken, expiresIn);

		// Then
		assertThat(token).isNotNull();
		assertThat(token.getProvider()).isEqualTo(provider);
		assertThat(token.getAccessToken()).isEqualTo(accessToken);
		assertThat(token.getRefreshToken()).isEqualTo(refreshToken);
		assertThat(token.getExpiresIn()).isEqualTo(expiresIn);
		assertThat(token.getIssuedAt()).isNotNull();
	}

	@Test
	void create_WhenAccessTokenIsNull_ThenReturnNull() {
		// Given
		OAuthProvider provider = OAuthProvider.KAKAO;
		String accessToken = null;
		String refreshToken = "valid-refresh-token";
		Long expiresIn = 3600L;

		// When
		OAuthToken token = OAuthToken.create(provider, accessToken, refreshToken, expiresIn);

		// Then
		assertThat(token).isNull();
	}
}
