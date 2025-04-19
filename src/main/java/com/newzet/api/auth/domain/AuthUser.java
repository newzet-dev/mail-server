package com.newzet.api.auth.domain;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

//TODO: 추후 email, role 등 추가 고려
@Getter
@Builder
public class AuthUser {
	private UUID id;

	public static AuthUser from(Token token) {
		return new AuthUser(UUID.fromString(token.getSubject()));
	}
}
