package com.newzet.api.auth.business.dto;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PROTECTED)
public record OAuthLoginResponse(
	String accessToken,
	String refreshToken,
	UUID OAuthMappingEntityId,
	boolean needRegister
) {
	public static OAuthLoginResponse toJwt(String accessToken, String refreshToken) {
		return OAuthLoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.needRegister(false)
			.build();
	}

	public static OAuthLoginResponse toSignUp(UUID OAuthMappingEntityId) {
		return OAuthLoginResponse.builder()
			.OAuthMappingEntityId(OAuthMappingEntityId)
			.needRegister(true)
			.build();
	}
}
