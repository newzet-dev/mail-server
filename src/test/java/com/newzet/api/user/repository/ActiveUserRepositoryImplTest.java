package com.newzet.api.user.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.newzet.api.exception.user.NoActiveUserException;
import com.newzet.api.user.domain.ActiveUser;
import com.newzet.api.user.domain.UserStatus;

class ActiveUserRepositoryImplTest {

	private UserJpaRepository userJpaRepository;
	private ActiveUserRepositoryImpl activeUserRepository;

	@BeforeEach
	void setUp() {
		userJpaRepository = Mockito.mock(UserJpaRepository.class);
		activeUserRepository = new ActiveUserRepositoryImpl(userJpaRepository);
	}

	@Test
	public void email과_일치하는_활성_유저가_있는_경우_활성_유저를_리턴() {
		//Given
		String existEmail = "exist@example.com";
		when(userJpaRepository.findByEmailAndStatus(existEmail, UserStatus.ACTIVE))
			.thenReturn(Optional.of(new UserEntity(1L, existEmail, UserStatus.ACTIVE)));

		//When
		ActiveUser activeUser = activeUserRepository.findActiveUserByEmail(existEmail);

		//Then
		assertEquals(existEmail, activeUser.getEmail());
		assertEquals(ActiveUser.class, activeUser.getClass());
	}

	@Test
	public void email과_일치하는_활성_유저가_없는_경우_NoActiveUserException_예외_발생() {
		String noExistEmail = "noExist@example.com";
		when(userJpaRepository.findByEmailAndStatus(noExistEmail, UserStatus.ACTIVE))
			.thenReturn(Optional.empty());

		//When
		assertThrows(NoActiveUserException.class, () ->
			activeUserRepository.findActiveUserByEmail(noExistEmail));
	}
}
