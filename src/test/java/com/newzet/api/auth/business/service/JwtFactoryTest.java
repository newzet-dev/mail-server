package com.newzet.api.auth.business.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.newzet.api.auth.domain.Token;
import com.newzet.api.auth.domain.TokenType;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtFactoryTest {

	private JwtFactory jwtFactory;
	private String TEST_SECRET;
	private SecretKey testSecretKey;

	@BeforeEach
	void setUp() {
		TEST_SECRET = "testsecretkeymustbelongerthan256bitstomakeitwork00000";
		jwtFactory = new JwtFactory(TEST_SECRET);
		testSecretKey = Keys.hmacShaKeyFor(TEST_SECRET.getBytes());
	}

	@Test
	public void createAccessToken_returnValidAccessToken() {
		//Given
		String userId = "user123";

		//When
		Token token = jwtFactory.createAccessToken(userId);

		//Then
		assertThat(token).isNotNull();
		assertThat(token.getType()).isEqualTo(TokenType.ACCESS);
		assertThat(token.getSubject()).isEqualTo(userId);
		assertThat(token.isExpired()).isFalse();
		assertThat(token.isAccessToken()).isTrue();
	}

	@Test
	public void createRefreshToken_returnValidRefreshToken() {
		//Given
		String userId = "user123";

		//When
		Token token = jwtFactory.createRefreshToken(userId);

		//Then
		assertThat(token).isNotNull();
		assertThat(token.getType()).isEqualTo(TokenType.REFRESH);
		assertThat(token.getSubject()).isEqualTo(userId);
		assertThat(token.isExpired()).isFalse();
		assertThat(token.isRefreshToken()).isTrue();
	}

	@Test
	public void parseToken_whenValidToken_returnToken() {
		//Given
		String userId = "user123";
		Token originalToken = jwtFactory.createAccessToken(userId);
		String tokenValue = originalToken.getValue();

		//When
		Optional<Token> parsedToken = jwtFactory.parseToken(tokenValue);

		//Then
		assertThat(parsedToken).isPresent();
		assertThat(parsedToken.get().getSubject()).isEqualTo(originalToken.getSubject());
		assertThat(parsedToken.get().getType()).isEqualTo(originalToken.getType());
	}

	@Test
	public void parseToken_whenInvalidToken_returnEmpty() {
		//Given
		String invalidToken = "invalid.token.value";

		//When
		Optional<Token> parsedToken = jwtFactory.parseToken(invalidToken);

		//Then
		assertThat(parsedToken).isEmpty();
	}

	@Test
	public void parseToken_whenNullToken_returnEmpty() {
		//Given
		String nullToken = null;

		//When
		Optional<Token> parsedToken = jwtFactory.parseToken(nullToken);

		//Then
		assertThat(parsedToken).isEmpty();
	}

	@Test
	public void parseToken_whenTokenWithDifferentSignature_returnEmpty() {
		//Given
		String userId = "user123";
		Token originalToken = jwtFactory.createAccessToken(userId);
		String tokenValue = originalToken.getValue();

		String tampered =
			tokenValue.substring(0, tokenValue.lastIndexOf('.') + 1) + "invalidSignature";

		//When
		Optional<Token> parsedToken = jwtFactory.parseToken(tampered);

		//Then
		assertThat(parsedToken).isEmpty();
	}

	@Test
	public void verifyTokenType_whenAccessTokenIsUsedAsRefresh_returnFalse() {
		//Given
		String userId = "user123";
		Token accessToken = jwtFactory.createAccessToken(userId);

		//When & Then
		assertThat(accessToken.isRefreshToken()).isFalse();
	}

	@Test
	public void verifyTokenType_whenRefreshTokenIsUsedAsAccess_returnFalse() {
		//Given
		String userId = "user123";
		Token refreshToken = jwtFactory.createRefreshToken(userId);

		//When & Then
		assertThat(refreshToken.isAccessToken()).isFalse();
	}

	@Test
	public void parseToken_whenEmptyToken_returnEmpty() {
		//Given
		String emptyToken = "";

		//When
		Optional<Token> parsedToken = jwtFactory.parseToken(emptyToken);

		//Then
		assertThat(parsedToken).isEmpty();
	}

	@Test
	public void parseToken_whenMalformedToken_returnEmpty() {
		//Given
		String malformedToken = "not.a.validJWTtoken";

		//When
		Optional<Token> parsedToken = jwtFactory.parseToken(malformedToken);

		//Then
		assertThat(parsedToken).isEmpty();
	}

	@Test
	public void parseToken_whenExpiredToken_returnEmpty() {
		//Given
		String userId = "user123";
		Date past = new Date(System.currentTimeMillis() - 1000 * 60 * 60); // 1 hour ago

		String expiredToken = Jwts.builder()
			.subject(userId)
			.issuedAt(past)
			.expiration(past)  // Already expired
			.claim("type", TokenType.ACCESS.name())
			.signWith(testSecretKey)
			.compact();

		//When
		Optional<Token> parsedToken = jwtFactory.parseToken(expiredToken);

		//Then
		assertThat(parsedToken).isEmpty();
	}

	@Test
	public void parseToken_whenMissingTypeField_returnEmpty() {
		//Given
		String userId = "user123";
		Date now = new Date();
		Date future = new Date(now.getTime() + 1000 * 60 * 60);

		String tokenWithoutType = Jwts.builder()
			.subject(userId)
			.issuedAt(now)
			.expiration(future)
			// No type claim
			.signWith(testSecretKey)
			.compact();

		//When
		Optional<Token> parsedToken = jwtFactory.parseToken(tokenWithoutType);

		//Then
		assertThat(parsedToken).isEmpty();
	}

	@Test
	public void parseToken_whenInvalidTypeValue_returnEmpty() {
		//Given
		String userId = "user123";
		Date now = new Date();
		Date future = new Date(now.getTime() + 1000 * 60 * 60);

		String tokenWithInvalidType = Jwts.builder()
			.subject(userId)
			.issuedAt(now)
			.expiration(future)
			.claim("type", "INVALID_TYPE")  // Invalid type value
			.signWith(testSecretKey)
			.compact();

		//When
		Optional<Token> parsedToken = jwtFactory.parseToken(tokenWithInvalidType);

		//Then
		assertThat(parsedToken).isEmpty();
	}
}
