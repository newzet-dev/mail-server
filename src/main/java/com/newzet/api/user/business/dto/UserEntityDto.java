package com.newzet.api.user.business.dto;

import com.newzet.api.user.business.service.UserFactory;
import com.newzet.api.user.domain.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntityDto {
	private final Long id;
	private final String email;
	private final String status;

	public static UserEntityDto create(Long id, String email, String status) {
		return UserEntityDto.builder()
			.id(id)
			.email(email)
			.status(status)
			.build();
	}

	public User toDomain() {
		return UserFactory.create(id, email, status);
	}
}
