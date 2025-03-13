package com.newzet.api.user.domain;

import com.newzet.api.user.business.dto.UserEntityDto;

public interface User {
	public UserEntityDto toEntityDto();

	public Long getId();
}
