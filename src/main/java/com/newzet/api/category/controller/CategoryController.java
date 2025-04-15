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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping("/category")
	public ResponseEntity<CategoryListResponse> getCategoryList() {
		List<Category> categoryList = categoryService.getCategories();
		CategoryListResponse categoryListResponse = CategoryListResponse.create(
			categoryList.stream()
				.map(category -> CategoryResponse.create(category.getId(),
					category.getName(), category.getImageUrl(), category.getEmoji()))
				.collect(Collectors.toList()));
		return ResponseEntity.ok(categoryListResponse);
	}
}
