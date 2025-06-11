package com.newzet.api.category.presentation.mapper;

import java.util.List;

import com.newzet.api.category.domain.Category;
import com.newzet.api.category.presentation.dto.CategoryListResponse;
import com.newzet.api.category.presentation.dto.CategoryResponse;

public class CategoryResponseMapper {
	public static CategoryResponse toResponse(Category category) {
		return new CategoryResponse(category.id(), category.name(), category.imageUrl(), category.emoji());
	}

	public static CategoryListResponse toListResponse(List<CategoryResponse> categoryResponseList) {
		return new CategoryListResponse(categoryResponseList);
	}
}
