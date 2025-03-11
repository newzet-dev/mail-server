package com.newzet.api.user.repository.repository;

import com.newzet.api.user.business.dto.UserEntityDto;

public interface UserRepository {
	UserEntityDto save(String email, String status);

	UserEntityDto getByEmail(String email);
}
