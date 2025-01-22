package com.newzet.api.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.newzet.api.exception.user.InvalidUserException;

class UserRepositoryImplTest {

	@Test
	public void 이메일로_존재하는_활성_유저_조회() {
		//given
		UserRepository userRepository = new UserRepositoryImpl(new MockJpaRepository());
		String email = "exist@example.com";

		//when
		ActiveUser activeUser = userRepository.findActiveUserByEmail(email);

		//then
		Assertions.assertEquals(activeUser.getEmail(), email);
	}

	@Test
	public void 존재하지_않는_이메일의_활성_유저_조회() {
		// given
		UserRepository userRepository = new UserRepositoryImpl(new MockJpaRepository());
		String email = "no_exist@example.com";

		//then
		Assertions.assertThrows(InvalidUserException.class,
			() -> userRepository.findActiveUserByEmail(email));
	}

}
