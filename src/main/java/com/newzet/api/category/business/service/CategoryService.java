package com.newzet.api.category.business.service;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.category.business.CategoryRepository;
import com.newzet.api.category.business.dto.CategoryEntityDto;
import com.newzet.api.category.controller.dto.CategoryListResponse;
import com.newzet.api.category.controller.dto.CategoryResponse;
import com.newzet.api.category.domain.Category;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryListResponse getCategories() {
		List<Category> categoryList = categoryRepository.findAll().stream()
			.map(CategoryEntityDto::toDomain)
			.toList();
		return CategoryListResponse.create(categoryList.stream()
			.map(category -> CategoryResponse.create(category.getId(),
				category.getName(), category.getImageUrl(), category.getEmoji()))
			.collect(toList()));
	}

}
