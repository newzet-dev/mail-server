package com.newzet.api.auth.business.dto;

import lombok.Builder;

@Builder
public record JWTResponseDTO(
	String accessToken,
	String refreshToken
) {
}
