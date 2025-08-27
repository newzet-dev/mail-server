package com.newzet.api.category.presentation.dto;

import java.util.List;

public record CategoryListResponse(
	List<CategoryResponse> categoryList
) {
}
