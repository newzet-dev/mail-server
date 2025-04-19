package com.newzet.api.user.business.dto;

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

	public static UniqueMailResponse ofInActive() {
		return new UniqueMailResponse(false, "휴면 유저의 이메일입니다.");
	}

	public static UniqueMailResponse ofWithDrawn() {
		return new UniqueMailResponse(false, "탈퇴한 사용자의 이메일입니다.");
	}
}
