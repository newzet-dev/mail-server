package com.newzet.api.fcm.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class FcmTokenTest {

	@Test
	void create_WhenCalled_ThenCreateNewFcmToken() {
		// Given
		UUID userId = UUID.randomUUID();
		String value = "test-fcm-token";

		// When
		FcmToken fcmToken = FcmToken.create(userId, value);

		// Then
		assertThat(fcmToken.id()).isNull();
		assertThat(fcmToken.userId()).isEqualTo(userId);
		assertThat(fcmToken.value()).isEqualTo(value);
		assertThat(fcmToken.createdAt()).isNotNull();
	}

	@Test
	void constructor_WhenCalled_ThenCreateFcmTokenWithAllFields() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		String value = "test-fcm-token";
		LocalDateTime createdAt = LocalDateTime.now();

		// When
		FcmToken fcmToken = new FcmToken(id, createdAt, userId, value);

		// Then
		assertThat(fcmToken.id()).isEqualTo(id);
		assertThat(fcmToken.userId()).isEqualTo(userId);
		assertThat(fcmToken.value()).isEqualTo(value);
		assertThat(fcmToken.createdAt()).isEqualTo(createdAt);
	}

	@Test
	void changeUserId_WhenCalled_ThenReturnNewTokenWithUpdatedUserId() {
		// Given
		UUID originalUserId = UUID.randomUUID();
		UUID newUserId = UUID.randomUUID();
		UUID id = UUID.randomUUID();
		String value = "test-fcm-token";
		LocalDateTime createdAt = LocalDateTime.now();

		FcmToken originalToken = new FcmToken(id, createdAt, originalUserId, value);

		// When
		FcmToken updatedToken = originalToken.changeUserId(newUserId);

		// Then
		assertThat(updatedToken.id()).isEqualTo(id);
		assertThat(updatedToken.userId()).isEqualTo(newUserId);
		assertThat(updatedToken.value()).isEqualTo(value);
		assertThat(updatedToken.createdAt()).isEqualTo(createdAt);

		// Original token should remain unchanged
		assertThat(originalToken.userId()).isEqualTo(originalUserId);
	}
}
