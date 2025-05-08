package com.newzet.api.usercategory.business;

import java.util.List;
import java.util.UUID;

import com.newzet.api.usercategory.business.dto.UserCategoryEntityDto;

public interface UserCategoryRepository {

	List<UserCategoryEntityDto> getUserCategoryListByUserId(UUID userId);

	void deleteUserCategoryByUserId(UUID userId);

	void addUserCategory(UUID userId, List<UUID> categoryIds);
}
