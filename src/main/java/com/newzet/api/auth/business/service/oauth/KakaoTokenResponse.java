package com.newzet.api.auth.business.service.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenResponse(
	@JsonProperty("access_token") String accessToken,
	@JsonProperty("refresh_token") String refreshToken,
	@JsonProperty("expires_in") Long expiresIn,
	@JsonProperty("refresh_token_expires_in") Long refreshTokenExpiresIn,
	@JsonProperty("token_type") String tokenType,
	String scope
) {
}
