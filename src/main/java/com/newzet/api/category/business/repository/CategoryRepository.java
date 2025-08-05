package com.newzet.api.category.business.repository;

import java.util.List;
import java.util.UUID;

import com.newzet.api.category.domain.Category;

public interface CategoryRepository {

	List<Category> findAll();

	List<Category> findCategoryListByIdList(List<UUID> idList);

	Category findById(UUID id);
}
