package com.newzet.api.userinfo.presentation.dto;

import java.util.List;

import com.newzet.api.category.presentation.dto.CategoryResponse;

public record UserinfoWithCategoryListResponse(
	String nickname,
	String email,
	List<CategoryResponse> categoryList
) {
}
