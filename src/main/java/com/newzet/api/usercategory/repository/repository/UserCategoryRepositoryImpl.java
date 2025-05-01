package com.newzet.api.usercategory.repository.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.category.repository.CategoryEntity;
import com.newzet.api.category.repository.CategoryJpaRepository;
import com.newzet.api.category.repository.exception.NoCategoryException;
import com.newzet.api.user.exception.NoUserException;
import com.newzet.api.user.repository.entity.UserEntity;
import com.newzet.api.user.repository.repository.UserJpaRepository;
import com.newzet.api.usercategory.business.UserCategoryRepository;
import com.newzet.api.usercategory.business.dto.UserCategoryEntityDto;
import com.newzet.api.usercategory.repository.entity.UserCategoryEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserCategoryRepositoryImpl implements UserCategoryRepository {

	private final UserCategoryJpaRepository userCategoryJpaRepository;
	private final UserJpaRepository userJpaRepository;
	private final CategoryJpaRepository categoryJpaRepository;

	@Override
	public List<UserCategoryEntityDto> getUserCategoryListByUserId(UUID userId) {
		return userCategoryJpaRepository.findByUserId(userId).stream()
			.map(UserCategoryEntity::toEntityDto)
			.toList();
	}

	@Override
	public void deleteUserCategoryByUserId(UUID userId) {
		userCategoryJpaRepository.deleteByUserId(userId);
	}

	@Override
	public void addUserCategory(UUID userId, List<UUID> categoryIds) {
		UserEntity userEntity = userJpaRepository.findById(userId)
			.orElseThrow(() -> new NoUserException("존재하지 않는 user 입니다."));
		List<UserCategoryEntity> userCategoryEntityList = categoryIds.stream().map(categoryId -> {
			CategoryEntity categoryEntity = categoryJpaRepository.findById(categoryId)
				.orElseThrow(() -> new NoCategoryException("존재하지 않는 category 입니다."));
			return UserCategoryEntity.create(userEntity,
				categoryEntity);
		}).toList();
		userCategoryJpaRepository.saveAll(userCategoryEntityList);
	}
}
