package com.newzet.api.category.controller.dto;

import java.util.List;
import java.util.UUID;

public record CategoryListResponse(
	List<CategoryResponse> categoryList
) {
	public static CategoryListResponse create(List<CategoryResponse> categoryList) {
		return new CategoryListResponse(categoryList);
	}
}
