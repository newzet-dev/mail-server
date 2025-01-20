package com.newzet.api.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.newzet.api.exception.user.WithDrawnUserException;

public class WithDrawnUserTest {

	@Test
	@DisplayName("탈퇴 유저는 검증 시 WithDrawnUserException")
	void verify() {
		//given
		User user = new WithDrawnUser();

		//when
		Assertions.assertThrows(WithDrawnUserException.class, user::verify);
	}
}
