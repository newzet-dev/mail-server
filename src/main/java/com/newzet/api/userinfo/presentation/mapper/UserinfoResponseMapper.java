package com.newzet.api.userinfo.presentation.mapper;

import java.util.List;

import com.newzet.api.category.domain.Category;
import com.newzet.api.category.presentation.dto.CategoryResponse;
import com.newzet.api.category.presentation.mapper.CategoryResponseMapper;
import com.newzet.api.userinfo.domain.Userinfo;
import com.newzet.api.userinfo.presentation.dto.UserinfoWithCategoryListResponse;

public class UserinfoResponseMapper {

	public static UserinfoWithCategoryListResponse toWithCategoryListResponse(Userinfo userinfo,
		List<Category> categoryList) {
		List<CategoryResponse> categoryResponses = categoryList.stream()
			.map(CategoryResponseMapper::toResponse)
			.toList();
		return new UserinfoWithCategoryListResponse(userinfo.nickname(), userinfo.email(), categoryResponses);
	}
}
