package com.newzet.api.usercategory.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.usercategory.business.repository.UserCategoryRepository;
import com.newzet.api.usercategory.domain.UserCategory;
import com.newzet.api.usercategory.jpa.entity.UserCategoryEntity;
import com.newzet.api.usercategory.jpa.mapper.UserCategoryEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserCategoryRepositoryImpl implements UserCategoryRepository {

	private final UserCategoryJpaRepository userCategoryJpaRepository;

	@Override
	public List<UserCategory> findUserCategoryListByUserId(UUID userId) {
		return userCategoryJpaRepository.findByUserId(userId).stream()
			.map(UserCategoryEntityMapper::toDomain)
			.toList();
	}

	@Override
	public void deleteUserCategoryByUserId(UUID userId) {
		userCategoryJpaRepository.deleteByUserId(userId);
	}

	@Override
	public List<UserCategory> addUserCategory(UUID userId, List<UUID> categoryIds) {
		List<UserCategoryEntity> userCategoryEntityList = categoryIds.stream()
			.map(categoryId -> UserCategoryEntity.create(userId, categoryId))
			.toList();
		return userCategoryJpaRepository.saveAll(userCategoryEntityList).stream()
			.map(UserCategoryEntityMapper::toDomain)
			.toList();
	}
}
