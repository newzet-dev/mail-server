package com.newzet.api.common.auth.domain;

import java.util.Date;
import java.util.UUID;

import com.newzet.api.common.auth.exception.TokenBadRequestException;
import com.newzet.api.common.util.UuidConverter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Token {
	private final UUID subject;
	private final String name;
	private final Date issuedAt;
	private final Date expiredAt;

	public static Token of(String subject, String name, Date issuedAt, Date expiredAt) {
		if (subject == null || name == null || issuedAt == null || expiredAt == null) {
			throw new TokenBadRequestException("토큰 생성에 필요한 값이 누락되었습니다.");
		}
		return new Token(UuidConverter.convert(subject), name, issuedAt, expiredAt);
	}

	public boolean isExpired() {
		return expiredAt.before(new Date());
	}
}
