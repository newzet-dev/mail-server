package com.newzet.api.user;

import org.springframework.stereotype.Component;

import com.newzet.api.exception.user.NoActiveUserException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEntityRepository {

	private final UserJpaRepository userJpaRepository;

	public UserJpaEntity findActiveUserByEmail(String email) {
		return userJpaRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
			.orElseThrow(NoActiveUserException::new);
	}
}
