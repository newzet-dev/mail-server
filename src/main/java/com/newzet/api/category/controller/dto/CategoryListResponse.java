package com.newzet.api.category.controller.dto;

import java.util.List;
import java.util.UUID;

public record CategoryListResponse(
	List<CategoryResponse> categoryList
) {
	public static CategoryListResponse create(List<CategoryResponse> categoryList) {
		return new CategoryListResponse(categoryList);
	}

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
}
