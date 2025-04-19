package com.newzet.api.user.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class ActiveUserTest {

	@Test
	public void create_returnActiveUser() {
		//Given
		UUID id = UUID.randomUUID();
		String email = "test@example.com";
		String nickName = "testName";

		//When
		ActiveUser activeUser = ActiveUser.create(id, email, nickName);

		//Then
		assertEquals(id, activeUser.getId());
		assertEquals(email, activeUser.getEmail());
	}
}
