package com.newzet.api.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.newzet.api.exception.user.InActiveUserException;
import com.newzet.api.exception.user.WithDrawnUserException;

class ActiveUserVerifierTest {

	private ActiveUserVerifier activeUserVerifier;

	@BeforeEach
	void setUp() {
		activeUserVerifier = new ActiveUserVerifier();
	}

	@Test
	@DisplayName("활성 유저인 경우 성공")
	void 활성_유저() {
		//given
		User user = User.activeUser();

		//when
		activeUserVerifier.verify(user);
	}

	@Test
	@DisplayName("유저가 휴먼인 경우 실패")
	void 휴먼_유저() {
		//given
		User user = User.user(UserStatus.DORMANT);

		//when
		Assertions.assertThrows(InActiveUserException.class, () -> activeUserVerifier.verify(user));
	}

	@Test
	@DisplayName("유저가 탈퇴한 경우 실패")
	void 탈퇴_유저() {
		//given
		User user = User.user(UserStatus.WITHDRAWN);

		//when
		Assertions.assertThrows(WithDrawnUserException.class,
			() -> activeUserVerifier.verify(user));
	}
}
