package com.newzet.api.auth.business.dto;

public record JwtRefreshRequestDTO(
	String refreshToken,
	String deviceType
) {
}
