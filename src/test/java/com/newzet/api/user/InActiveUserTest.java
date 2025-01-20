package com.newzet.api.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.newzet.api.exception.user.InActiveUserException;

public class InActiveUserTest {

	@Test
	@DisplayName("비활성 유저는 검증 시 InActiveUserException")
	void verify() {
		//given
		User user = new InActiveUser();

		//when
		Assertions.assertThrows(InActiveUserException.class, user::verify);
	}
}
