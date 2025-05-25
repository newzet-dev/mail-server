package com.newzet.api.common.auth.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.newzet.api.common.auth.exception.TokenBadRequestException;

class AuthorizationHeaderParserTest {

	private final String AUTH_PREFIX = "Bearer ";
	private final AuthorizationHeaderParser parser = new AuthorizationHeaderParser();

	@Test
	void extractHeaderValue_whenValidBearerToken_returnsToken() {
		// Given
		String value = "valid";
		String authHeader = AUTH_PREFIX + value;

		// When
		String result = parser.extractAuthHeader(authHeader);

		// Then
		Assertions.assertEquals(value, result);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"Vearer invalid", "Bearer "})
	void extractHeaderValue_whenHeaderIsMissing_throwsException(String input) {
		// When & Then
		Assertions.assertThrows(TokenBadRequestException.class,
			() -> parser.extractAuthHeader(input));
	}
}
