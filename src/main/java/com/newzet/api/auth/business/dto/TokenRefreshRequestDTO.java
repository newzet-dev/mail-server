package com.newzet.api.auth.business.dto;

public record TokenRefreshRequestDTO(
	String refreshToken,
	String deviceType
) {
}
