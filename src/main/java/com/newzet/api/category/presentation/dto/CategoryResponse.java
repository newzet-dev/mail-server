package com.newzet.api.category.presentation.dto;

import java.util.UUID;

public record CategoryResponse(
	UUID id,
	String name,
	String imageUrl,
	String emoji
) {
}
