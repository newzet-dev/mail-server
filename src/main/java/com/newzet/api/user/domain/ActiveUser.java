package com.newzet.api.user.domain;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ActiveUser {

	private final String email;

	public static ActiveUser create(String email) {
		return new ActiveUser(email);
	}
}
