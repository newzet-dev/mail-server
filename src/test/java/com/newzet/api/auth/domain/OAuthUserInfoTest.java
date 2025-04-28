package com.newzet.api.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.newzet.api.auth.exception.OAuthErrorException;

class OAuthUserInfoTest {

    @Test
    void create_WhenValidParameters_ThenCreateOAuthUserInfo() {
        // Given
        String socialUserId = "oauth-user-id";
        String email = "user@example.com";
        String name = "User Name";
        OAuthProvider provider = OAuthProvider.KAKAO;
        OAuthToken oauthToken = OAuthToken.builder()
                .provider(provider)
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenPrefix("bearer")
                .expiresIn(3600L)
                .scope("profile")
                .issuedAt(LocalDateTime.now())
                .build();

        // When
        OAuthUserInfo userInfo = OAuthUserInfo.create(socialUserId, email, name, provider, oauthToken);

        // Then
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getSocialUserId()).isEqualTo(socialUserId);
        assertThat(userInfo.getEmail()).isEqualTo(email);
        assertThat(userInfo.getName()).isEqualTo(name);
        assertThat(userInfo.getProvider()).isEqualTo(provider);
        assertThat(userInfo.getOAuthToken()).isEqualTo(oauthToken);
    }

    @Test
    void create_WhenSocialUserIdIsNull_ThenThrowOAuthErrorException() {
        // Given
        String socialUserId = null;
        String email = "user@example.com";
        String name = "User Name";
        OAuthProvider provider = OAuthProvider.KAKAO;
        OAuthToken oauthToken = OAuthToken.builder()
                .provider(provider)
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenPrefix("bearer")
                .expiresIn(3600L)
                .scope("profile")
                .issuedAt(LocalDateTime.now())
                .build();

        // When, Then
        assertThatThrownBy(() -> OAuthUserInfo.create(socialUserId, email, name, provider, oauthToken))
                .isInstanceOf(OAuthErrorException.class)
                .hasMessage("응답이 올바르지 않아 socialUserId가 전달되지 않았습니다.");
    }

    @Test
    void create_WhenNameIsNull_ThenUseDefaultName() {
        // Given
        String socialUserId = "oauth-user-id";
        String email = "user@example.com";
        String name = null;
        OAuthProvider provider = OAuthProvider.KAKAO;
        OAuthToken oauthToken = OAuthToken.builder()
                .provider(provider)
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenPrefix("bearer")
                .expiresIn(3600L)
                .scope("profile")
                .issuedAt(LocalDateTime.now())
                .build();

        // When
        OAuthUserInfo userInfo = OAuthUserInfo.create(socialUserId, email, name, provider, oauthToken);

        // Then
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getSocialUserId()).isEqualTo(socialUserId);
        assertThat(userInfo.getEmail()).isEqualTo(email);
        assertThat(userInfo.getName()).isEqualTo("Unknown");
        assertThat(userInfo.getProvider()).isEqualTo(provider);
        assertThat(userInfo.getOAuthToken()).isEqualTo(oauthToken);
    }

    @Test
    void create_WhenEmailIsNull_ThenAcceptNullEmail() {
        // Given
        String socialUserId = "oauth-user-id";
        String email = null;
        String name = "User Name";
        OAuthProvider provider = OAuthProvider.KAKAO;
        OAuthToken oauthToken = OAuthToken.builder()
                .provider(provider)
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenPrefix("bearer")
                .expiresIn(3600L)
                .scope("profile")
                .issuedAt(LocalDateTime.now())
                .build();

        // When
        OAuthUserInfo userInfo = OAuthUserInfo.create(socialUserId, email, name, provider, oauthToken);

        // Then
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getSocialUserId()).isEqualTo(socialUserId);
        assertThat(userInfo.getEmail()).isNull();
        assertThat(userInfo.getName()).isEqualTo(name);
        assertThat(userInfo.getProvider()).isEqualTo(provider);
        assertThat(userInfo.getOAuthToken()).isEqualTo(oauthToken);
    }
}