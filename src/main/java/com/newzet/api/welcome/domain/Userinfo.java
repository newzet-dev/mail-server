package com.newzet.api.welcome.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Userinfo {
	private UUID id;
	private String email;
	private String nickname;
	private UserRole role;
	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;

	public void changeEmail(String email) {
		this.email = email.trim();
	}

	public void changeNickname(String nickname) {
		this.nickname = nickname.trim();
	}

	public void delete() {
		this.deletedAt = LocalDateTime.now();
	}
}
