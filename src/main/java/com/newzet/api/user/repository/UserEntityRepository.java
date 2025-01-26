package com.newzet.api.user.repository;

import org.springframework.stereotype.Component;

import com.newzet.api.exception.user.NoActiveUserException;
import com.newzet.api.user.domain.UserStatus;

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
