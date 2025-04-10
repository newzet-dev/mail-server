package com.newzet.api.auth.business.dto;

public record JwtResponseDTO(
	String accessToken,
	String refreshToken
) {
}
