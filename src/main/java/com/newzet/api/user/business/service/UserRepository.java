package com.newzet.api.user.business.service;

import java.util.Optional;
import java.util.UUID;

import com.newzet.api.user.business.dto.UserEntityDto;

public interface UserRepository {
	UserEntityDto save(String email, String nickName, String status);

	void update(UserEntityDto userEntityDto);

	void delete(UUID userId);

	UserEntityDto getByEmail(String email);

	Optional<UserEntityDto> findOptionalByEmail(String email);

	UserEntityDto getById(UUID userId);
}
