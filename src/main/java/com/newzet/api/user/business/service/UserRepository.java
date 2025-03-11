package com.newzet.api.user.business.service;

import com.newzet.api.user.business.dto.UserEntityDto;

public interface UserRepository {
	UserEntityDto save(String email, String status);

	UserEntityDto getByEmail(String email);
}
