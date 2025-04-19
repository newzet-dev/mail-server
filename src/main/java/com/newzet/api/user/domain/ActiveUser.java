package com.newzet.api.user.domain;

import java.util.UUID;

import com.newzet.api.user.business.dto.UserEntityDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ActiveUser implements User {

	private final UUID id;
	private final String email;
	private final String nickname;

	public static ActiveUser create(UUID id, String email, String nickname) {
		return new ActiveUser(id, email, nickname);
	}

	@Override
	public UserEntityDto toEntityDto() {
		return UserEntityDto.create(id, email, nickname, "ACTIVE");
	}
}
