package com.newzet.api.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InActiveUser implements User {

	private final String email;

	public static InActiveUser create(String email) {
		return new InActiveUser(email);
	}
}
