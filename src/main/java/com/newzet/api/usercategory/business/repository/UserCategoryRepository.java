package com.newzet.api.usercategory.business.repository;

import java.util.List;
import java.util.UUID;

import com.newzet.api.usercategory.domain.UserCategory;

public interface UserCategoryRepository {

	List<UserCategory> findUserCategoryListByUserId(UUID userId);

	void deleteUserCategoryByUserId(UUID userId);

	List<UserCategory> addUserCategory(UUID userId, List<UUID> categoryIds);
}
