package com.newzet.api.userinfo.presentation.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UniqueMailResponseTest {

	@Test
	@DisplayName("이메일이 고유할 때(true), isUnique는 true이고 '사용 가능한 이메일입니다.' 메시지를 반환한다.")
	void create_whenEmailIsUnique_shouldReturnTrueAndAvailableMessage() {
		// Given
		boolean isUnique = true;

		// When
		UniqueMailResponse response = UniqueMailResponse.create(isUnique);

		// Then
		assertNotNull(response);
		assertTrue(response.isUnique());
		assertEquals("사용 가능한 이메일입니다.", response.message());
	}

	@Test
	@DisplayName("이메일이 고유하지 않을 때(false), isUnique는 false이고 '사용 중인 이메일입니다.' 메시지를 반환한다.")
	void create_whenEmailIsNotUnique_shouldReturnFalseAndInUseMessage() {
		// Given
		boolean isUnique = false;

		// When
		UniqueMailResponse response = UniqueMailResponse.create(isUnique);

		// Then
		assertNotNull(response);
		assertFalse(response.isUnique());
		assertEquals("사용 중인 이메일입니다.", response.message());
	}
}
