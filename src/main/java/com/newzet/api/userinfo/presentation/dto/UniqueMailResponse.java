package com.newzet.api.userinfo.presentation.dto;

public record UniqueMailResponse(
	boolean isUnique,
	String message
) {
	public static UniqueMailResponse create(boolean uniqueness) {
		if (uniqueness) {
			return new UniqueMailResponse(true, "사용 가능한 이메일입니다.");
		}
		return new UniqueMailResponse(false, "사용 중인 이메일입니다.");
	}
}
