package com.newzet.api.user.domain;

import java.util.UUID;

import com.newzet.api.user.business.dto.UserEntityDto;

public interface User {
	public UserEntityDto toEntityDto();

	public UUID getId();
}
