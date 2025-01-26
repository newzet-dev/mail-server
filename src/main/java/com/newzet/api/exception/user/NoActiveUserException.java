package com.newzet.api.exception.user;

public class NoActiveUserException extends InvalidUserException {
	public NoActiveUserException() {
		super("해당하는 활성 사용자가 없습니다.");
	}
}
