package com.newzet.api.category.business.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.category.business.repository.CategoryRepository;
import com.newzet.api.category.domain.Category;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public List<Category> getCategoryList() {
		return categoryRepository.findAll();
	}

	public List<Category> getCategoryListByIdList(List<UUID> idList) {
		return categoryRepository.getCategoryListByIdList(idList);
	}

	public Category getCategoryById(UUID id) {
		return categoryRepository.getById(id);
	}
}
