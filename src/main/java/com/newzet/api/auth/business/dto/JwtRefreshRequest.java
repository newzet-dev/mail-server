package com.newzet.api.auth.business.dto;

public record JwtRefreshRequest(
	String refreshToken,
	String deviceType
) {
}
