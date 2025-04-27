package com.newzet.api.auth.domain;

import com.newzet.api.auth.exception.OAuthException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
public class OAuthUserInfo {
	private String socialUserId;
	private String email;
	private String name;
	private OAuthProvider provider;
	private OAuthToken oAuthToken;

	public static OAuthUserInfo create(String socialUserId, String email, String name,
		OAuthProvider provider, OAuthToken oauthToken) {

		if (socialUserId == null) {
			throw new OAuthException("응답에서 socialUserId를 찾을 수 없습니다.");
		}

		return OAuthUserInfo.builder()
			.socialUserId(socialUserId)
			.email(email)
			.name(name == null ? "Unknown" : name)
			.provider(provider)
			.oAuthToken(oauthToken)
			.build();
	}
}
