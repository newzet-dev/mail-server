package com.newzet.api.usercategory.business.dto;

import java.util.UUID;

import com.newzet.api.category.business.dto.CategoryEntityDto;
import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.usercategory.domain.UserCategory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCategoryEntityDto {
	private final UUID id;
	private final UserEntityDto user;
	private final CategoryEntityDto category;

	public static UserCategoryEntityDto create(UUID id, UserEntityDto user, CategoryEntityDto category) {
		return new UserCategoryEntityDto(id, user, category);
	}

	public UserCategory toDomain() {
		return UserCategory.create(id, user.toDomain(), category.toDomain());
	}

}
