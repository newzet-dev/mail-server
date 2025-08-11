package com.newzet.api.common.auth.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.newzet.api.common.auth.exception.TokenBadRequestException;

class TokenTest {
	private static final UUID SUBJECT_UUID = UUID.randomUUID();
	private static final String SUBJECT = SUBJECT_UUID.toString();
	private static final String NAME = "test";
	private static final Date PAST = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
	private static final Date FUTURE = new Date(System.currentTimeMillis() + 60 * 60 * 1000);

	@Test
	void of_whenAllValuesValid_returnsToken() {
		//When
		Token token = Token.of(SUBJECT, PAST, FUTURE);

		//Then
		assertEquals(SUBJECT_UUID, token.getSubject());
		assertEquals(PAST, token.getIssuedAt());
		assertEquals(FUTURE, token.getExpiredAt());
	}

	@Test
	void of_whenSubjectIsNull_throwsException() {
		assertThrows(TokenBadRequestException.class,
			() -> Token.of(null, PAST, FUTURE));
	}

	@Test
	void of_whenIssuedAtIsNull_throwsException() {
		assertThrows(TokenBadRequestException.class,
			() -> Token.of(SUBJECT, null, FUTURE));
	}

	@Test
	void of_whenExpiredAtIsNull_throwsException() {
		assertThrows(TokenBadRequestException.class,
			() -> Token.of(SUBJECT, PAST, null));
	}

	@Test
	void isExpired_whenExpirationInPast_returnsTrue() {
		// given
		Token token = Token.of(SUBJECT, PAST, PAST);

		// when + then
		assertThat(token.isExpired()).isTrue();
	}

	@Test
	void isExpired_whenExpirationInFuture_returnsFalse() {
		// given
		Date expiredAt = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
		Token token = Token.of(SUBJECT, PAST, FUTURE);

		// when + then
		assertThat(token.isExpired()).isFalse();
	}
}
