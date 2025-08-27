package com.newzet.api.common.auth.business;

import static com.newzet.api.common.auth.business.TokenConverterTest.TestFixture.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.newzet.api.common.auth.domain.Token;
import com.newzet.api.common.auth.exception.TokenBadRequestException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class TokenConverterTest {

	private final TokenConverter converter = new TokenConverter(SECRET);

	@Test
	public void toToken_whenValidToken_returnToken() {
		//Given
		String validTokenValue = createValidToken();

		//When
		Token token = converter.toToken(validTokenValue);

		//Then
		assertEquals(USER_ID, token.getSubject());
		assertEquals(PAST.getTime(), token.getIssuedAt().getTime());
		assertEquals(FUTURE.getTime(), token.getExpiredAt().getTime());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"invalid.token.value"})
	public void toToken_whenInvalidToken_throwTokenBadRequestException(String input) {
		//When + Then
		assertThrows(TokenBadRequestException.class,
			() -> converter.toToken(input));
	}

	static class TestFixture {
		static final UUID USER_ID = UUID.randomUUID();
		static final String RAW_SECRET = "testsecretkeymustbelongerthan256bitstomakeitwork00000";
		static final String SECRET = Base64.getEncoder().encodeToString(RAW_SECRET.getBytes());
		static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
		static final Date PAST = new Date(
			((System.currentTimeMillis() - 60 * 60 * 1000) / 1000) * 1000);
		static final Date FUTURE = new Date(
			((System.currentTimeMillis() + 60 * 60 * 1000) / 1000) * 1000);

		static String createValidToken() {
			return Jwts.builder()
				.subject(USER_ID.toString())
				.issuedAt(PAST)
				.expiration(FUTURE)
				.signWith(SECRET_KEY)
				.compact();
		}
	}
}
