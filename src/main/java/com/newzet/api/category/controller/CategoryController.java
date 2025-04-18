package com.newzet.api.category.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.category.business.service.CategoryService;
import com.newzet.api.category.controller.dto.CategoryListResponse;
import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "카테고리", description = "카테고리 관련 API")
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping("/category")
	@Operation(summary = "카테고리 리스트 조회",
		description = "모든 카테고리들을 담은 리스트를 조회한다.")
	public ResponseEntity<SuccessResponse<CategoryListResponse>> getCategoryList() {
		CategoryListResponse categoryListResponse = categoryService.getCategories();
		SuccessResponse<CategoryListResponse> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "카테고리 목록 조회 성공", categoryListResponse);
		return ResponseEntity.ok(response);
	}
}
