package com.newzet.api.auth.domain;

import java.util.Date;

import com.newzet.api.auth.business.dto.TokenDTO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Token {
	private final TokenType type;
	private final String value;
	private final String subject;
	private final Date issueAt;
	private final Date expiration;

	public static Token of(TokenType type, String value, String subject, Date issueAt,
		Date expiration) {
		return new Token(type, value, subject, issueAt, expiration);
	}

	public boolean isExpired() {
		return expiration.before(new Date());
	}

	public boolean isAccessToken() {
		return this.type.equals(TokenType.ACCESS);
	}

	public boolean isRefreshToken() {
		return this.type.equals(TokenType.REFRESH);
	}

	public boolean isSameValue(String value) {
		return this.value.equals(value);
	}

	public TokenDTO toTokenDTO() {
		return TokenDTO.from(this.value);
	}
}
