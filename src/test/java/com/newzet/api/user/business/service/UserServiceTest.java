package com.newzet.api.user.business.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.userinfo.presentation.dto.UniqueMailResponse;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Test
	void getUserIdByEmail_WhenEmailExists_ThenReturnUser() {
		//Given
		String email = "test@example.com";
		UUID userId = UUID.randomUUID();
		UserEntityDto userEntityDto = UserEntityDto.create(
			userId, email, "testUser", "ACTIVE"
		);
		when(userRepository.getByEmail(email)).thenReturn(userEntityDto);

		//When
		UUID foundedUserId = userService.getUserIdByEmail(email);

		//Then
		assertEquals(userId, foundedUserId);
	}

	@Test
	void checkEmailUniqueness_WhenEmailNotExists_ThenReturnUnique() {
		//Given
		String email = "test@example.com";
		when(userRepository.findOptionalByEmail(email)).thenReturn(Optional.empty());

		//When
		UniqueMailResponse response = userService.checkEmailUniqueness(email);

		//Then
		assertTrue(response.isUnique());
		assertEquals("사용 가능한 이메일입니다.", response.message());
	}

	@Test
	void checkEmailUniqueness_WhenEmailWithdrawn_ThenReturnWithdrawn() {
		//Given
		String email = "test@example.com";
		UserEntityDto withdrawnUser = UserEntityDto.create(
			UUID.randomUUID(), email, "testUser", "WITHDRAWN"
		);
		when(userRepository.findOptionalByEmail(email)).thenReturn(Optional.of(withdrawnUser));

		//When
		UniqueMailResponse response = userService.checkEmailUniqueness(email);

		//Then
		assertFalse(response.isUnique());
		assertEquals("탈퇴한 사용자의 이메일입니다.", response.message());
	}

	@Test
	void checkEmailUniqueness_WhenEmailInactive_ThenReturnInactive() {
		//Given
		String email = "test@example.com";
		UserEntityDto inactiveUser = UserEntityDto.create(
			UUID.randomUUID(), email, "testUser", "INACTIVE"
		);
		when(userRepository.findOptionalByEmail(email)).thenReturn(Optional.of(inactiveUser));

		//When
		UniqueMailResponse response = userService.checkEmailUniqueness(email);

		//Then
		assertFalse(response.isUnique());
		assertEquals("휴면 유저의 이메일입니다.", response.message());
	}

	@Test
	void checkEmailUniqueness_WhenEmailActive_ThenReturnDuplicate() {
		//Given
		String email = "test@example.com";
		UserEntityDto activeUser = UserEntityDto.create(
			UUID.randomUUID(), email, "testUser", "ACTIVE"
		);
		when(userRepository.findOptionalByEmail(email)).thenReturn(Optional.of(activeUser));

		//When
		UniqueMailResponse response = userService.checkEmailUniqueness(email);

		//Then
		assertFalse(response.isUnique());
		assertEquals("사용 중인 이메일입니다.", response.message());
	}
}
