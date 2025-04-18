package com.newzet.api.category.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.newzet.api.category.business.CategoryRepository;
import com.newzet.api.category.business.dto.CategoryEntityDto;
import com.newzet.api.category.repository.exception.NoCategoryException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

	private final CategoryJpaRepository categoryJpaRepository;

	@Override
	public List<CategoryEntityDto> findAll() {
		List<CategoryEntity> categoryEntities = categoryJpaRepository.findAll();
		return categoryEntities.stream()
			.map(CategoryEntity::toCategoryEntityDto)
			.toList();
	}
}
