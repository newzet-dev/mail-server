package com.newzet.api.category.orchestrator;

import java.util.List;

import org.springframework.stereotype.Service;

import com.newzet.api.category.business.service.CategoryService;
import com.newzet.api.category.presentation.dto.CategoryListResponse;
import com.newzet.api.category.presentation.dto.CategoryResponse;
import com.newzet.api.category.presentation.mapper.CategoryResponseMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryOrchestrator {

	private final CategoryService categoryService;

	public CategoryListResponse getCategoryList() {
		List<CategoryResponse> categoryResponseList = categoryService.getCategoryList().stream()
			.map(CategoryResponseMapper::toResponse)
			.toList();
		return CategoryResponseMapper.toListResponse(categoryResponseList);
	}
}
