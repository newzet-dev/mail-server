package com.newzet.api.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.newzet.api.exception.user.InvalidUserException;

class UserEntityRepositoryTest {

	private UserEntityRepository userEntityRepository;

	@BeforeEach
	public void setUp() {
		userEntityRepository = new UserEntityRepository(new MockJpaRepository());
	}

	@Test
	public void 이메일로_존재하는_활성_유저_조회() {
		//given
		String email = "exist@example.com";

		//when
		UserJpaEntity userEntity = userEntityRepository.findActiveUserByEmail(email);

		//then
		Assertions.assertEquals(userEntity.getEmail(), email);
	}

	@Test
	public void 존재하지_않는_이메일의_활성_유저_조회() {
		// given
		String email = "no_exist@example.com";

		//then
		Assertions.assertThrows(InvalidUserException.class,
			() -> userEntityRepository.findActiveUserByEmail(email));
	}

}
