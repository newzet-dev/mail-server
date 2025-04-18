package com.newzet.api.category.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.category.business.service.CategoryService;
import com.newzet.api.category.controller.dto.CategoryListResponse;
import com.newzet.api.category.controller.dto.CategoryListResponse.CategoryResponse;
import com.newzet.api.category.domain.Category;

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
	public ResponseEntity<CategoryListResponse> getCategoryList() {
		CategoryListResponse categoryListResponse = categoryService.getCategories();
		return ResponseEntity.ok(categoryListResponse);
	}
}
