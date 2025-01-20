package com.newzet.api.exception.user;

public class InActiveUserException extends InvalidUserException {
	public InActiveUserException() {
		super("휴먼 사용자 입니다.");
	}
}
