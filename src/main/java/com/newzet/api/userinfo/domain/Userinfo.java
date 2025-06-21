package com.newzet.api.userinfo.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record Userinfo(
	UUID id,
	String email,
	String nickname,
	UserRole role,
	LocalDateTime createdAt,
	LocalDateTime deletedAt
) {
	public void changeEmail(String email) {
		email = email.trim();
	}

	public void changeNickname(String nickname) {
		nickname = nickname.trim();
	}
}
