package com.newzet.api.auth.domain;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PUBLIC)
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
}
