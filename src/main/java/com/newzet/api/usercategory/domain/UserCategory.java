package com.newzet.api.usercategory.domain;

import java.util.UUID;

import com.newzet.api.category.domain.Category;
import com.newzet.api.user.domain.User;
import com.newzet.api.usercategory.business.dto.UserCategoryEntityDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCategory {
	private final UUID id;
	private final User user;
	private final Category category;

	public static UserCategory create(UUID id, User user, Category category) {
		return new UserCategory(id, user, category);
	}

	public UserCategoryEntityDto toEntityDto() {
		return UserCategoryEntityDto.create(id, user.toEntityDto(), category.toEntityDto());
	}

}
