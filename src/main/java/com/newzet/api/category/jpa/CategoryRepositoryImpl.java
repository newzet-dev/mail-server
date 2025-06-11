package com.newzet.api.category.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.category.business.repository.CategoryRepository;
import com.newzet.api.category.domain.Category;
import com.newzet.api.category.jpa.exception.NoCategoryException;
import com.newzet.api.category.jpa.mapper.CategoryEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

	private final CategoryJpaRepository jpaRepository;

	@Override
	public List<Category> findAll() {
		return jpaRepository.findAll().stream()
			.map(CategoryEntityMapper::toDomain)
			.toList();
	}

	@Override
	public List<Category> getCategoryListByIdList(List<UUID> idList) {
		return jpaRepository.findAllById(idList).stream()
			.map(CategoryEntityMapper::toDomain)
			.toList();
	}

	@Override
	public Category getById(UUID id) {
		return jpaRepository.findById(id)
			.map(CategoryEntityMapper::toDomain)
			.orElseThrow(() -> new NoCategoryException("No category found with id: " + id));
	}
}
