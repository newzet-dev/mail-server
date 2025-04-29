package com.newzet.api.category.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.newzet.api.category.business.CategoryRepository;
import com.newzet.api.category.business.dto.CategoryEntityDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

	private final CategoryJpaRepository categoryJpaRepository;

	@Override
	public List<CategoryEntityDto> findAll() {
		List<CategoryEntity> categoryEntities = categoryJpaRepository.findAll();
		return categoryEntities.stream()
			.map(CategoryEntity::toEntityDto)
			.toList();
	}
}
