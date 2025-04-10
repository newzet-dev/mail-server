package com.newzet.api.auth.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

class AuthUserTest {

	@Test
	public void from_whenValidToken_returnAuthUser() {
		//Given
		String userId = "123";
		Token token = Token.of(TokenType.ACCESS, "token-value", userId, new Date(), new Date());

		//When
		AuthUser authUser = AuthUser.from(token);

		//Then
		assertThat(authUser.getId()).isEqualTo(userId);
	}

	@Test
	public void builder_whenValidInput_returnAuthUser() {
		//Given
		String userId = "123";

		//When
		AuthUser authUser = AuthUser.builder()
			.id(userId)
			.build();

		//Then
		assertThat(authUser.getId()).isEqualTo(userId);
	}
}
