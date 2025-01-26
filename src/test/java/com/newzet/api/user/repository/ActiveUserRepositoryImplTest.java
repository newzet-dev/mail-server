package com.newzet.api.user.repository;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.newzet.api.user.domain.ActiveUser;
import com.newzet.api.user.domain.UserStatus;

class ActiveUserRepositoryImplTest {

	private UserJpaRepository userJpaRepository;
	private ActiveUserRepositoryImpl activeUserRepository;

	@BeforeEach
	void setUp() {
		userJpaRepository = Mockito.mock(userJpaRepository.getClass());
		activeUserRepository = new ActiveUserRepositoryImpl(userJpaRepository);
	}

	@Test
	public void email과_일치하는_활성_유저가_있는_경우_활성_유저를_리턴() {
		//Given
		String existEmail = "exist@example.com";
		when(userJpaRepository.findByEmailAndStatus(existEmail, UserStatus.ACTIVE))
			.thenReturn(Optional.of(new UserJpaEntity(1L, existEmail, UserStatus.ACTIVE)));

		//When
		ActiveUser activeUser = activeUserRepository.findActiveUserByEmail(existEmail);

		//Then
		Assertions.assertEquals(existEmail, activeUser.getEmail());
		Assertions.assertEquals(ActiveUser.class, activeUser.getClass());
	}
}
