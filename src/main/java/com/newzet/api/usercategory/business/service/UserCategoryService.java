package com.newzet.api.usercategory.business.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.usercategory.business.repository.UserCategoryRepository;
import com.newzet.api.usercategory.domain.UserCategory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCategoryService {

	private final UserCategoryRepository userCategoryRepository;

	public List<UserCategory> findCategoryListByUserId(UUID userId) {
		return userCategoryRepository.findUserCategoryListByUserId(userId);
	}

	public void deleteUserCategoriesByUserId(UUID userId) {
		userCategoryRepository.deleteUserCategoriesByUserId(userId);
	}

	public List<UserCategory> addUserCategories(UUID userId, List<UUID> categoryIdList) {
		List<UserCategory> userCategories = categoryIdList.stream()
			.map(categoryId -> UserCategory.create(userId, categoryId))
			.toList();
		return userCategoryRepository.addUserCategories(userCategories);
	}
}
