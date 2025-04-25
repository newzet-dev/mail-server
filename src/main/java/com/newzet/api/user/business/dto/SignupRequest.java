package com.newzet.api.user.business.dto;

import com.newzet.api.auth.domain.OAuthProvider;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignupRequest(
	@NotBlank
	@Email
	String email,

	@NotBlank
	String nickname,

	@NotBlank
	String oAuthMappingEntityId,

	@NotNull
	OAuthProvider provider,

	@NotBlank
	String deviceType
) {
}
