package com.newzet.api.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.user.business.dto.UserEntityDto;

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

		//When
		UserEntityDto savedUser = userRepository.save(email, status);

		//Then
		assertEquals(email, savedUser.getEmail());
		assertEquals(status, savedUser.getStatus());
	}

	@Test
	public void getByEmail_whenUserExist_returnUserEntityDto() {
		//Given
		String email = "exist@example.com";
		String status = "ACTIVE";
		UserEntityDto savedUser = userRepository.save(email, status);

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
}
