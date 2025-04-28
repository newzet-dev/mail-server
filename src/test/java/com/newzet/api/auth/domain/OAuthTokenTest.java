package com.newzet.api.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.newzet.api.auth.exception.OAuthErrorException;

class OAuthTokenTest {

    @Test
    void ofKakao_WhenValidParameters_ThenCreateOAuthToken() {
        // Given
        String accessToken = "valid-access-token";
        String refreshToken = "valid-refresh-token";
        Long expiresIn = 3600L;
        String tokenType = "bearer";
        String scope = "profile";

        // When
        OAuthToken token = OAuthToken.ofKakao(accessToken, refreshToken, expiresIn, tokenType, scope);

        // Then
        assertThat(token).isNotNull();
        assertThat(token.getProvider()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(token.getAccessToken()).isEqualTo(accessToken);
        assertThat(token.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(token.getExpiresIn()).isEqualTo(expiresIn);
        assertThat(token.getTokenPrefix()).isEqualTo(tokenType);
        assertThat(token.getScope()).isEqualTo(scope);
        assertThat(token.getIssuedAt()).isNotNull();
    }

    @Test
    void ofKakao_WhenAccessTokenIsNull_ThenThrowOAuthErrorException() {
        // Given
        String accessToken = null;
        String refreshToken = "valid-refresh-token";
        Long expiresIn = 3600L;
        String tokenType = "bearer";
        String scope = "profile";

        // When, Then
        assertThatThrownBy(() -> OAuthToken.ofKakao(accessToken, refreshToken, expiresIn, tokenType, scope))
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
        String tokenType = "bearer";
        String scope = "profile";

        // When
        OAuthToken token = OAuthToken.create(provider, accessToken, refreshToken, expiresIn, tokenType, scope);

        // Then
        assertThat(token).isNotNull();
        assertThat(token.getProvider()).isEqualTo(provider);
        assertThat(token.getAccessToken()).isEqualTo(accessToken);
        assertThat(token.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(token.getExpiresIn()).isEqualTo(expiresIn);
        assertThat(token.getTokenPrefix()).isEqualTo(tokenType);
        assertThat(token.getScope()).isEqualTo(scope);
        assertThat(token.getIssuedAt()).isNotNull();
    }

    @Test
    void create_WhenAccessTokenIsNull_ThenReturnNull() {
        // Given
        OAuthProvider provider = OAuthProvider.KAKAO;
        String accessToken = null;
        String refreshToken = "valid-refresh-token";
        Long expiresIn = 3600L;
        String tokenType = "bearer";
        String scope = "profile";

        // When
        OAuthToken token = OAuthToken.create(provider, accessToken, refreshToken, expiresIn, tokenType, scope);

        // Then
        assertThat(token).isNull();
    }
}