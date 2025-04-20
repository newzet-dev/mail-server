package com.newzet.api.user.business.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignupRequest(
	@NotBlank
	@Email
	String email,

	@NotBlank
	String nickname,

	@NotBlank
	String deviceType
) {
}
