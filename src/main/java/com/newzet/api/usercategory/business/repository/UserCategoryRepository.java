package com.newzet.api.usercategory.business.repository;

import java.util.List;
import java.util.UUID;

import com.newzet.api.usercategory.domain.UserCategory;

public interface UserCategoryRepository {

	List<UserCategory> findUserCategoryListByUserId(UUID userId);

	void deleteUserCategoriesByUserId(UUID userId);

	List<UserCategory> addUserCategories(List<UserCategory> userCategories);
}
