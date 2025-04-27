package com.newzet.api.auth.domain;

import java.time.LocalDateTime;

import com.newzet.api.auth.exception.OAuthException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
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
			throw new OAuthException("카카오 토큰 응답이 올바르지 않습니다.");
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
}
