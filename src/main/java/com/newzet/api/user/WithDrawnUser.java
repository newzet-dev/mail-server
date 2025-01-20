package com.newzet.api.user;

import com.newzet.api.exception.user.WithDrawnUserException;

public class WithDrawnUser implements User {
	@Override
	public void verify() {
		throw new WithDrawnUserException();
	}
}
