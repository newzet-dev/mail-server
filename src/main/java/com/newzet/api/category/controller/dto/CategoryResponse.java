package com.newzet.api.category.controller.dto;

import java.util.UUID;

public record CategoryResponse(
	String id,
	String name,
	String imageUrl,
	String emoji
) {
	public static CategoryResponse create(UUID id, String name, String imageUrl, String emoji) {
		return new CategoryResponse(id.toString(), name, imageUrl, emoji);
	}
}