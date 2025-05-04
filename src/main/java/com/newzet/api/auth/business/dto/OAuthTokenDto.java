package com.newzet.api.auth.business.dto;

import com.newzet.api.auth.domain.OAuthToken;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record OAuthTokenDto(
	String accessToken,
	String refreshToken,
	long expiresIn) {

	public static OAuthTokenDto from(OAuthToken oAuthToken) {
		return OAuthTokenDto.builder()
			.accessToken(oAuthToken.getAccessToken())
			.refreshToken(oAuthToken.getRefreshToken())
			.expiresIn(oAuthToken.getExpiresIn())
			.build();
	}
}
