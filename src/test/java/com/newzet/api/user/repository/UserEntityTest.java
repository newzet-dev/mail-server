package com.newzet.api.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.newzet.api.user.domain.ActiveUser;
import com.newzet.api.user.domain.UserStatus;

class UserEntityTest {

	@Test
	public void ActiveUser_변환() {
		//Given
		UserEntity userEntity = new UserEntity(1L, "test@example.com", UserStatus.ACTIVE);

		//When
		ActiveUser activeUser = userEntity.toActiveUser();

		//Then
		assertEquals(1L, activeUser.getId());
		assertEquals("test@example.com", activeUser.getEmail());
		assertEquals(ActiveUser.class, activeUser.getClass());
	}
}
