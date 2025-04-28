package com.newzet.api.auth.domain;

import java.time.LocalDateTime;

import com.newzet.api.auth.exception.OAuthErrorException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuthToken {
	private final OAuthProvider provider;
	private final String accessToken;
	private final String refreshToken;
	private final String tokenPrefix;
	private final Long expiresIn;
	private final String scope;
	private final LocalDateTime issuedAt;

	public static OAuthToken ofKakao(String accessToken, String refreshToken, Long expiresIn,
		String tokenType, String scope) {

		if (accessToken == null) {
			throw new OAuthErrorException("응답이 올바르지 않아 accessToken이 전달되지 않았습니다.");
		}

		return OAuthToken.builder()
			.provider(OAuthProvider.KAKAO)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.expiresIn(expiresIn)
			.tokenPrefix(tokenType)
			.scope(scope)
			.issuedAt(LocalDateTime.now())
			.build();
	}

	public static OAuthToken create(OAuthProvider provider, String accessToken, String refreshToken,
		Long expiresIn, String tokenType, String scope) {

		if (accessToken == null) {
			return null;
		}

		return OAuthToken.builder()
			.provider(provider)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.expiresIn(expiresIn)
			.tokenPrefix(tokenType)
			.scope(scope)
			.issuedAt(LocalDateTime.now())
			.build();
	}
}
