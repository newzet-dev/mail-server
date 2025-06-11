package com.newzet.api.category.business.repository;

import java.util.List;
import java.util.UUID;

import com.newzet.api.category.domain.Category;

public interface CategoryRepository {

	List<Category> findAll();

	List<Category> getCategoryListByIdList(List<UUID> idList);

	Category getById(UUID id);
}
