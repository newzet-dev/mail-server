package com.newzet.api.usercategory.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.category.repository.CategoryEntity;
import com.newzet.api.category.repository.CategoryJpaRepository;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.user.repository.entity.UserEntity;
import com.newzet.api.user.repository.repository.UserJpaRepository;
import com.newzet.api.usercategory.repository.repository.UserCategoryRepositoryImpl;

@DataJpaTest
@Import(UserCategoryRepositoryImpl.class)
@ExtendWith(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserCategoryRepositoryImplTest {

	@Autowired
	private UserCategoryRepositoryImpl userCategoryRepository;
	@Autowired
	private UserJpaRepository userJpaRepository;
	@Autowired
	private CategoryJpaRepository categoryJpaRepository;

	private UserEntity user;
	private List<CategoryEntity> categories;

	@BeforeEach
	void setUp() {
		user = userJpaRepository.save(UserEntity.create("test@example.com", "test", "ACTIVE"));
		categories = new ArrayList<>();

		for (int i = 1; i <= 5; i++) {
			categories.add(categoryJpaRepository.save(
				CategoryEntity.create("testCategory" + i, "testurl", "testemoji")));
		}
	}

	@Test
	void create_user_category_and_no_throw_any_exception() {
		//given
		List<UUID> categoryIds = categories.stream().map(CategoryEntity::getId).toList();
		UserEntity userEntity = userJpaRepository.findById(user.getId()).get();

		//when then
		assertEquals(userEntity.getId(), user.getId());
		assertDoesNotThrow(() -> userCategoryRepository.addUserCategory(userEntity.getId(),categoryIds));
	}

	@Test
	void get_user_categories_exactly_have_saved_numbers() {
		//given
		List<UUID> categoryIds = categories.stream().map(CategoryEntity::getId).toList();
		UserEntity userEntity = userJpaRepository.findById(user.getId()).get();

		//when
		userCategoryRepository.addUserCategory(userEntity.getId(),categoryIds);

		// then
		assertEquals(5, userCategoryRepository.getUserCategoryListByUserId(userEntity.getId()).size());
	}

	@Test
	void delete_user_category_and_exists_nothing() {
		//given
		List<UUID> categoryIds = categories.stream().map(CategoryEntity::getId).toList();
		UserEntity userEntity = userJpaRepository.findById(user.getId()).get();
		userCategoryRepository.addUserCategory(userEntity.getId(),categoryIds);

		//when then
		assertEquals(5, userCategoryRepository.getUserCategoryListByUserId(userEntity.getId()).size());
		userCategoryRepository.deleteUserCategoryByUserId(userEntity.getId());
		assertEquals(0, userCategoryRepository.getUserCategoryListByUserId(userEntity.getId()).size());
	}

}