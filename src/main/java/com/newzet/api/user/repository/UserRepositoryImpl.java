package com.newzet.api.user.repository;

import org.springframework.stereotype.Repository;

import com.newzet.api.user.business.dto.UserEntityDto;

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
		return null;
	}
}
