package com.newzet.api.auth.business.dto;

public record JwtResponse(
	String accessToken,
	String refreshToken
) {
}
