package com.newzet.api.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

	private final UserStatus status;

	public static User user(UserStatus status) {
		return new User(status);
	}

	public static User activeUser() {
		return new User(UserStatus.ACTIVE);
	}
}
