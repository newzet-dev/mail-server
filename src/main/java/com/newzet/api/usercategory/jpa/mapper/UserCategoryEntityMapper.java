package com.newzet.api.usercategory.jpa.mapper;

import com.newzet.api.usercategory.domain.UserCategory;
import com.newzet.api.usercategory.jpa.entity.UserCategoryEntity;

public class UserCategoryEntityMapper {
	public static UserCategory toDomain(UserCategoryEntity entity) {
		return new UserCategory(entity.getId(), entity.getUserId(), entity.getCategoryId());
	}
}
