package com.newzet.api.auth.domain;

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
		return OAuthUserInfo.builder()
			.socialUserId(socialUserId)
			.email(email)
			.name(name)
			.provider(provider)
			.oAuthToken(oauthToken)
			.build();
	}
}
