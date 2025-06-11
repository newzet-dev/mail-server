package com.newzet.api.category.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.category.orchestrator.CategoryOrchestrator;
import com.newzet.api.category.presentation.dto.CategoryListResponse;
import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "카테고리", description = "카테고리 관련 API")
public class CategoryController {

	private final CategoryOrchestrator categoryOrchestrator;

	@GetMapping("/category")
	@Operation(summary = "카테고리 리스트 조회",
		description = "모든 카테고리들을 담은 리스트를 조회한다.")
	public SuccessResponse<CategoryListResponse> getCategoryList() {
		CategoryListResponse response = categoryOrchestrator.getCategoryList();
		return SuccessResponse.create(
			ResponseCode.SUCCESS, "카테고리 목록 조회 성공", response);
	}
}
