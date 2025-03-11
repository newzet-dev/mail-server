package com.newzet.api.user.repository;

public class NoUserException extends RuntimeException {
	public NoUserException(String route) {
		super(String.format("[%s]-일치하는 사용자가 없습니다.", route));
	}
}
