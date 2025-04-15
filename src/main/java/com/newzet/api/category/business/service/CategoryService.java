package com.newzet.api.category.business.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.category.business.CategoryRepository;
import com.newzet.api.category.business.dto.CategoryEntityDto;
import com.newzet.api.category.domain.Category;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

	private final CategoryRepository categoryRepository;

	List<Category> getCategories() {
		return categoryRepository.findAll().stream()
			.map(CategoryEntityDto::toDomain)
			.toList();
	}

}
