package com.newzet.api.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.user.exception.NoUserException;
import com.newzet.api.user.repository.repository.UserRepositoryImpl;

@DataJpaTest
@Import(UserRepositoryImpl.class)
@ExtendWith(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryImplTest {

	@Autowired
	private UserRepositoryImpl userRepository;

	@Test
	public void save() {
		//Given
		String email = "exist@example.com";
		String status = "ACTIVE";
		String nickName = "testName";

		//When
		UserEntityDto savedUser = userRepository.save(email, nickName, status);

		//Then
		assertEquals(email, savedUser.getEmail());
		assertEquals(status, savedUser.getStatus());
	}

	@Test
	public void getByEmail_whenUserExist_returnUserEntityDto() {
		//Given
		String email = "exist@example.com";
		String status = "ACTIVE";
		String nickName = "testName";

		UserEntityDto savedUser = userRepository.save(email, nickName, status);

		//When
		UserEntityDto searchedUser = userRepository.getByEmail(email);

		//Then
		assertEquals(email, searchedUser.getEmail());
		assertEquals(status, searchedUser.getStatus());
	}

	@Test
	public void getByEmail_whenUserNoExist_throwNoUserException() {
		//Given
		String email = "exist@example.com";

		//When, Then
		assertThrows(NoUserException.class, () -> userRepository.getByEmail(email));
	}

	@Test
	public void save_inactiveUser() {
		//Given
		String email = "inactive@example.com";
		String status = "INACTIVE";
		String nickName = "testName";

		//When
		UserEntityDto savedUser = userRepository.save(email, nickName, status);

		//Then
		assertEquals(email, savedUser.getEmail());
		assertEquals(status, savedUser.getStatus());
	}

	@Test
	public void save_withdrawnUser() {
		//Given
		String email = "withdrawn@example.com";
		String status = "WITHDRAWN";
		String nickName = "testName";

		//When
		UserEntityDto savedUser = userRepository.save(email, nickName, status);

		//Then
		assertEquals(email, savedUser.getEmail());
		assertEquals(status, savedUser.getStatus());
	}

	@Test
	public void getById_whenUserExist_returnUserEntityDto() {
		//Given
		String email = "exist@example.com";
		String status = "ACTIVE";
		String nickName = "testName";

		UserEntityDto savedUser = userRepository.save(email, nickName, status);
		UUID userId = savedUser.getId();

		//When
		UserEntityDto searchedUser = userRepository.getById(userId);

		//Then
		assertEquals(email, searchedUser.getEmail());
		assertEquals(status, searchedUser.getStatus());
	}

	@Test
	public void getById_whenUserNoExist_throwNoUserException() {
		//Given
		UUID randomId = UUID.randomUUID();

		//When, Then
		assertThrows(NoUserException.class, () -> userRepository.getById(randomId));
	}

	@Test
	public void update_whenUserExist_updateUserSuccessfully() {
		//Given
		String email = "update@example.com";
		String status = "ACTIVE";
		String nickName = "originalName";

		UserEntityDto savedUser = userRepository.save(email, nickName, status);

		String updatedNickName = "updatedName";
		UserEntityDto updateUser = UserEntityDto.create(
			savedUser.getId(),
			email,
			updatedNickName,
			status
		);

		//When
		userRepository.update(updateUser);
		UserEntityDto retrievedUser = userRepository.getByEmail(email);

		//Then
		assertEquals(updatedNickName, retrievedUser.getNickname());
	}

	@Test
	public void update_whenUserNoExist_throwNoUserException() {
		//Given
		UserEntityDto nonExistingUser = UserEntityDto.create(
			UUID.randomUUID(),
			"nonexist@example.com",
			"nonexistName",
			"ACTIVE"
		);

		//When, Then
		assertThrows(NoUserException.class, () -> userRepository.update(nonExistingUser));
	}

	@Test
	public void delete_whenUserExist_deleteUserSuccessfully() {
		//Given
		String email = "todelete@example.com";
		String status = "ACTIVE";
		String nickName = "deleteName";

		UserEntityDto savedUser = userRepository.save(email, nickName, status);
		UUID userId = savedUser.getId();

		//When
		userRepository.delete(userId);

		//Then
		assertThrows(NoUserException.class, () -> userRepository.getByEmail(email));
	}

	@Test
	public void delete_whenUserNoExist_throwNoUserException() {
		//Given
		UUID randomId = UUID.randomUUID();

		//When, Then
		assertThrows(NoUserException.class, () -> userRepository.delete(randomId));
	}

	@Test
	public void findOptionalByEmail_whenUserExist_returnOptionalWithUser() {
		//Given
		String email = "optional@example.com";
		String status = "ACTIVE";
		String nickName = "optionalName";

		userRepository.save(email, nickName, status);

		//When
		Optional<UserEntityDto> result = userRepository.findOptionalByEmail(email);

		//Then
		assertTrue(result.isPresent());
		assertEquals(email, result.get().getEmail());
	}

	@Test
	public void findOptionalByEmail_whenUserNoExist_returnEmptyOptional() {
		//Given
		String email = "nonexistent@example.com";

		//When
		Optional<UserEntityDto> result = userRepository.findOptionalByEmail(email);

		//Then
		assertTrue(result.isEmpty());
	}
}
