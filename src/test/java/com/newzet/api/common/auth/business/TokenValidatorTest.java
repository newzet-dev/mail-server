package com.newzet.api.common.auth.business;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.newzet.api.common.auth.domain.Token;
import com.newzet.api.common.auth.exception.TokenExpiredException;

class TokenValidatorTest {
	private static final String SUBJECT = UUID.randomUUID().toString();
	private static final String NAME = "test";
	private static final Date PAST = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
	private static final Date FUTURE = new Date(System.currentTimeMillis() + 60 * 60 * 1000);

	private final TokenValidator tokenValidator = new TokenValidator();

	@Test
	public void validate_whenToken_doNothing() {
		//Given
		Token token = Token.of(SUBJECT, PAST, FUTURE);

		//When, Then
		assertDoesNotThrow(() -> tokenValidator.validate(token));
	}

	@Test
	public void validate_whenTokenExpired_throwTokenExpiredException() {
		//Given
		Token token = Token.of(SUBJECT, PAST, PAST);

		//When, Then
		assertThrows(TokenExpiredException.class, () -> tokenValidator.validate(token));
	}
}
