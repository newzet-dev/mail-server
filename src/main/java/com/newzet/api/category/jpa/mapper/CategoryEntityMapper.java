package com.newzet.api.category.jpa.mapper;

import com.newzet.api.category.domain.Category;
import com.newzet.api.category.jpa.CategoryEntity;

public class CategoryEntityMapper {
	public static Category toDomain(CategoryEntity entity) {
		return new Category(entity.getId(), entity.getName(), entity.getImageUrl(), entity.getEmoji());
	}
}
