package com.newzet.api.user.domain;

public class ActiveUser extends User {

	private ActiveUser(Long id, String email) {
		super(id, email);
	}

	public static ActiveUser create(Long id, String email) {
		return new ActiveUser(id, email);
	}
}
