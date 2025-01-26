package com.newzet.api.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.newzet.api.user.domain.ActiveUser;
import com.newzet.api.user.domain.UserStatus;

class UserJpaEntityTest {

	@Test
	public void ActiveUser_변환() {
		//Given
		UserJpaEntity userJpaEntity = new UserJpaEntity(1L, "test@example.com", UserStatus.ACTIVE);

		//When
		ActiveUser activeUser = userJpaEntity.toActiveUser();

		//Then
		assertEquals(1L, activeUser.getId());
		assertEquals("test@example.com", activeUser.getEmail());
		assertEquals(ActiveUser.class, activeUser.getClass());
	}
}
