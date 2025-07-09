package com.newzet.api.usercategory.jpa;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.usercategory.domain.UserCategory;
import com.newzet.api.usercategory.jpa.repository.UserCategoryJpaRepository;
import com.newzet.api.usercategory.jpa.repository.UserCategoryRepositoryImpl;

@DataJpaTest
@Import(UserCategoryRepositoryImpl.class)
@ExtendWith(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserCategoryRepositoryImplTest {

	@Autowired
	private UserCategoryRepositoryImpl userCategoryRepository;

	@Autowired
	private UserCategoryJpaRepository userCategoryJpaRepository;

	private UUID testUserId;
	private List<UUID> testCategoryIds;
	private List<UserCategory> userCategoriesToSave;

	@BeforeEach
	void setUp() {
		testUserId = UUID.randomUUID();
		testCategoryIds = List.of(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID()
		);

		userCategoriesToSave = testCategoryIds.stream()
			.map(categoryId -> new UserCategory(
				null,
				testUserId,
				categoryId))
			.toList();
	}

	@Test
	@DisplayName("사용자 카테고리 목록을 추가하고, 예외가 발생하지 않아야 한다.")
	void addUserCategories_shouldAddUserCategoriesWithoutException() {
		// When
		List<UserCategory> savedUserCategories = userCategoryRepository.addUserCategories(userCategoriesToSave);

		// Then
		assertNotNull(savedUserCategories);
		assertEquals(userCategoriesToSave.size(), savedUserCategories.size());
	}

	@Test
	@DisplayName("사용자 ID로 카테고리 목록을 조회하면 저장된 개수와 일치해야 한다.")
	void findUserCategoryListByUserId_shouldReturnCorrectNumberOfCategories() {
		// Given
		userCategoryRepository.addUserCategories(userCategoriesToSave);

		// When
		List<UserCategory> foundUserCategories = userCategoryRepository.findUserCategoryListByUserId(testUserId);

		// Then
		assertEquals(5, foundUserCategories.size());
		foundUserCategories.forEach(uc -> assertEquals(testUserId, uc.getUserId()));
		List<UUID> foundCategoryIds = foundUserCategories.stream()
			.map(UserCategory::getCategoryId)
			.toList();
		assertTrue(foundCategoryIds.containsAll(testCategoryIds));
	}

	@Test
	@DisplayName("사용자 ID로 카테고리 목록을 삭제하면, 데이터가 존재하지 않아야 한다.")
	void deleteUserCategoriesByUserId_shouldDeleteAllUserCategories() {
		// Given
		List<UserCategory> savedUserCategories = userCategoryRepository.addUserCategories(userCategoriesToSave);
		assertEquals(5, savedUserCategories.size());

		// When
		userCategoryRepository.deleteUserCategoriesByUserId(testUserId);

		// Then
		List<UserCategory> remainingUserCategories = userCategoryRepository.findUserCategoryListByUserId(testUserId);
		assertTrue(remainingUserCategories.isEmpty());
	}
}
