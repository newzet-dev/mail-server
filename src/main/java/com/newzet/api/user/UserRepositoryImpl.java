package com.newzet.api.user;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	@Override
	public ActiveUser findActiveUserByEmail(String email) {
		return null;
	}
}
