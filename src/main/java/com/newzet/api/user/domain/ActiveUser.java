package com.newzet.api.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ActiveUser {

	private final Long id;
	private final String email;

	public static ActiveUser create(Long id, String email) {
		return new ActiveUser(id, email);
	}
}
