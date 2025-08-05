package com.newzet.api.usercategory.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.usercategory.business.repository.UserCategoryRepository;
import com.newzet.api.usercategory.domain.UserCategory;
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
	public void deleteUserCategoriesByUserId(UUID userId) {
		userCategoryJpaRepository.deleteByUserId(userId);
	}

	@Override
	public List<UserCategory> addUserCategories(List<UserCategory> userCategories) {
		return userCategoryJpaRepository.saveAll(
				userCategories.stream().map(UserCategoryEntityMapper::toEntity).toList())
			.stream().map(UserCategoryEntityMapper::toDomain).toList();
	}
}
