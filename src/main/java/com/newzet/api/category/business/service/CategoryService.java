package com.newzet.api.category.business.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.category.business.CategoryRepository;
import com.newzet.api.category.business.dto.CategoryCacheDto;
import com.newzet.api.category.business.dto.CategoryEntityDto;
import com.newzet.api.category.domain.Category;
import com.newzet.api.common.cache.CacheUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final CacheUtil cacheUtil;
	private static final int category_counts = 12;
	private static final String CACHE_DOMAIN_PREFIX = "category:";
	private static final Long CACHE_DURATION = 1000 * 60 * 60 * 24 * 7L;

	List<Category> getCategories() {
		List<Category> categoriesOnCache = getCategoriesOnCache();
		if (categoriesOnCache.isEmpty() || categoriesOnCache.size() != category_counts) {
			return getCategoriesInDatabase();
		}
		return categoriesOnCache;
	}

	List<Category> getCategoriesOnCache() {
		return cacheUtil.get(CACHE_DOMAIN_PREFIX, CategoryCacheDto.class).stream()
			.map(dto -> Category.create(dto.getId(), dto.getName(), dto.getImageUrl(),
				dto.getEmoji()))
			.toList();
	}

	List<Category> getCategoriesInDatabase() {
		return categoryRepository.findAll().stream()
			.map(CategoryEntityDto::toDomain)
			.peek(category -> cacheUtil.set(CACHE_DOMAIN_PREFIX + category.getName(), CategoryCacheDto.class,
				CACHE_DURATION))
			.toList();
	}
}
