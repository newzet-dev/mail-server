package com.newzet.api.user.domain;

import com.newzet.api.user.business.dto.UserEntityDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ActiveUser implements User {

	private final Long id;
	private final String email;

	public static ActiveUser create(Long id, String email) {
		return new ActiveUser(id, email);
	}

	@Override
	public UserEntityDto toEntityDto() {
		return UserEntityDto.create(id, email, "ACTIVE");
	}
}
