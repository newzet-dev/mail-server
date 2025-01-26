package com.newzet.api.user.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ActiveUserTest {

	@Test
	public void Active_User_생성() {
		//Given
		String email = "test@example.com";

		//When
		ActiveUser activeUser = ActiveUser.create(email);

		//Then
		Assertions.assertEquals(email, activeUser.getEmail());
	}
}
