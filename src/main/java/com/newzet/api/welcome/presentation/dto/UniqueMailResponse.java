package com.newzet.api.welcome.presentation.dto;

public record UniqueMailResponse(
	boolean isUnique,
	String message
) {
	public static UniqueMailResponse ofUnique() {
		return new UniqueMailResponse(true, "사용 가능한 이메일입니다.");
	}

	public static UniqueMailResponse ofDuplicate() {
		return new UniqueMailResponse(false, "사용 중인 이메일입니다.");
	}
}
