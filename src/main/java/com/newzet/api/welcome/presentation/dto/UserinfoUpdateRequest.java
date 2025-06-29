package com.newzet.api.welcome.presentation.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserinfoUpdateRequest(
	String email,
	@NotBlank String nickname,
	@NotNull List<UUID> userCategory
) {
}
