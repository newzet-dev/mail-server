package com.newzet.api.auth.domain;

import com.newzet.api.auth.exception.OAuthErrorException;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
			throw new OAuthErrorException("응답이 올바르지 않아 socialUserId가 전달되지 않았습니다.");
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
