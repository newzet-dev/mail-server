package com.newzet.api.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.newzet.api.exception.user.InActiveUserException;
import com.newzet.api.exception.user.WithDrawnUserException;

class ActiveUserVerifierTest {

	@Test
	@DisplayName("활성 유저인 경우 성공")
	void 활성_유저() {
		//given
		User user = new ActiveUser();

		//when
		user.verify();
	}

	@Test
	@DisplayName("유저가 휴먼인 경우 실패")
	void 휴먼_유저() {
		//given
		User user = new InActiveUser();

		//when
		Assertions.assertThrows(InActiveUserException.class, user::verify);
	}

	@Test
	@DisplayName("유저가 탈퇴한 경우 실패")
	void 탈퇴_유저() {
		//given
		User user = new WithDrawnUser();

		//when
		Assertions.assertThrows(WithDrawnUserException.class, user::verify);
	}
}
