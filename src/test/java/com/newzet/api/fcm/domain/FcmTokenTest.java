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
		assertThat(fcmToken.getId()).isNull();
		assertThat(fcmToken.getUserId()).isEqualTo(userId);
		assertThat(fcmToken.getValue()).isEqualTo(value);
		assertThat(fcmToken.getCreatedAt()).isNotNull();
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
		assertThat(fcmToken.getId()).isEqualTo(id);
		assertThat(fcmToken.getUserId()).isEqualTo(userId);
		assertThat(fcmToken.getValue()).isEqualTo(value);
		assertThat(fcmToken.getCreatedAt()).isEqualTo(createdAt);
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
		assertThat(updatedToken.getId()).isEqualTo(id);
		assertThat(updatedToken.getUserId()).isEqualTo(newUserId);
		assertThat(updatedToken.getValue()).isEqualTo(value);
		assertThat(updatedToken.getCreatedAt()).isEqualTo(createdAt);

		// Original token should remain unchanged
		assertThat(originalToken.getUserId()).isEqualTo(originalUserId);
	}
}
