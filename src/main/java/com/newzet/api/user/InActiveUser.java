package com.newzet.api.user;

import com.newzet.api.exception.user.InActiveUserException;

public class InActiveUser implements User {
	@Override
	public void verify() {
		throw new InActiveUserException();
	}
}
