package com.newzet.api.user;

import com.newzet.api.exception.user.NoActiveUserException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	@Override
	public ActiveUser findActiveUserByEmail(String email) {
		UserJpaEntity userJpaEntity = userJpaRepository.findByEmailAndStatus(email,
			UserStatus.ACTIVE).orElseThrow(NoActiveUserException::new);
		return null;
	}
}
