package com.newzet.api.exception.user;

public class WithDrawnUserException extends InvalidUserException {
	public WithDrawnUserException() {
		super("탈퇴한 사용자 입니다.");
	}
}
