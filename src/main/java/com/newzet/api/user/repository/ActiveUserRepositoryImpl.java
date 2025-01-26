package com.newzet.api.user.repository;

import org.springframework.stereotype.Component;

import com.newzet.api.exception.user.NoActiveUserException;
import com.newzet.api.user.domain.ActiveUser;
import com.newzet.api.user.domain.UserStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActiveUserRepositoryImpl implements ActiveUserRepository {

	private final UserJpaRepository userJpaRepository;

	public ActiveUser findActiveUserByEmail(String email) {
		UserJpaEntity userJpaEntity = userJpaRepository.findByEmailAndStatus(email,
				UserStatus.ACTIVE)
			.orElseThrow(NoActiveUserException::new);
		return userJpaEntity.toActiveUser();
	}
}
