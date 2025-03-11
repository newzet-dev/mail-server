package com.newzet.api.user.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ActiveUserTest {

	@Test
	public void create_returnActiveUser() {
		//Given
		Long id = 1L;
		String email = "test@example.com";

		//When
		ActiveUser activeUser = ActiveUser.create(id, email);

		//Then
		assertEquals(id, activeUser.getId());
		assertEquals(email, activeUser.getEmail());
	}
}
