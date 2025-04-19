package com.newzet.api.auth.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

class TokenTest {

	@Test
	public void of_returnToken() {
		//Given
		TokenType type = TokenType.ACCESS;
		String value = "token-value";
		String subject = "user-id";
		Date issueAt = new Date();
		Date expiration = new Date(System.currentTimeMillis() + 1000 * 60);

		//When
		Token token = Token.of(type, value, subject, issueAt, expiration);

		//Then
		assertThat(token.getType()).isEqualTo(type);
		assertThat(token.getValue()).isEqualTo(value);
		assertThat(token.getSubject()).isEqualTo(subject);
		assertThat(token.getIssueAt()).isEqualTo(issueAt);
		assertThat(token.getExpiration()).isEqualTo(expiration);
	}

	@Test
	public void isExpired_whenExpired_returnTrue() {
		//Given
		Date past = new Date(System.currentTimeMillis() - 1000 * 60);
		Token token = Token.of(TokenType.ACCESS, "value", "subject", new Date(), past);

		//When
		boolean isExpired = token.isExpired();

		//Then
		assertThat(isExpired).isTrue();
	}

	@Test
	public void isExpired_whenNotExpired_returnFalse() {
		//Given
		Date future = new Date(System.currentTimeMillis() + 1000 * 60);
		Token token = Token.of(TokenType.ACCESS, "value", "subject", new Date(), future);

		//When
		boolean isExpired = token.isExpired();

		//Then
		assertThat(isExpired).isFalse();
	}

	@Test
	public void isAccessToken_whenAccessToken_returnTrue() {
		//Given
		Token token = Token.of(TokenType.ACCESS, "value", "subject", new Date(), new Date());

		//When
		boolean isAccessToken = token.isAccessToken();

		//Then
		assertThat(isAccessToken).isTrue();
	}

	@Test
	public void isRefreshToken_whenRefreshToken_returnTrue() {
		//Given
		Token token = Token.of(TokenType.REFRESH, "value", "subject", new Date(), new Date());

		//When
		boolean isRefreshToken = token.isRefreshToken();

		//Then
		assertThat(isRefreshToken).isTrue();
	}
}
