package com.newzet.api.user.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateNicknameRequest(
	@NotBlank
	String nickname
) {
}
