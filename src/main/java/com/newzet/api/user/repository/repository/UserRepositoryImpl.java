package com.newzet.api.user.repository.repository;

import org.springframework.stereotype.Repository;

import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.user.repository.entity.UserEntity;
import com.newzet.api.user.repository.exception.NoUserException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	public UserEntityDto save(String email, String status) {
		UserEntity user = UserEntity.create(email, status);
		UserEntity savedUser = userJpaRepository.save(user);
		return UserEntityDto.create(savedUser.getId(), savedUser.getEmail(),
			savedUser.getStatus().name());
	}

	public UserEntityDto getByEmail(String email) {
		UserEntity user = userJpaRepository.findByEmail(email)
			.orElseThrow(() -> new NoUserException(this.getClass().getSimpleName() + "#" +
				Thread.currentThread().getStackTrace()[2].getMethodName()));
		return UserEntityDto.create(user.getId(), user.getEmail(), user.getStatus().name());
	}
}
