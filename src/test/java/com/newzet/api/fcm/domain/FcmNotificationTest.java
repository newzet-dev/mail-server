package com.newzet.api.fcm.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class FcmNotificationTest {

	@Test
	void create_WhenValidParameters_ThenCreateNotification() {
		// Given
		UUID userId = UUID.randomUUID();
		String token = "test-fcm-token";
		String title = "New Article";
		String body = "You have a new article";
		String data = "test-data";

		// When
		FcmNotification notification = FcmNotification.create(userId, token, title, body, data);

		// Then
		assertThat(notification.getUserId()).isEqualTo(userId);
		assertThat(notification.getToken()).isEqualTo(token);
		assertThat(notification.getTitle()).isEqualTo(title);
		assertThat(notification.getBody()).isEqualTo(body);
		assertThat(notification.getData()).isEqualTo(data);
		assertThat(notification.getCreatedAt()).isNotNull();
	}

	@Test
	void isValid_WhenTokenAndTitleNotEmpty_ThenReturnTrue() {
		// Given
		FcmNotification notification = FcmNotification.create(
			UUID.randomUUID(),
			"test-token",
			"Test Title",
			"Test Body",
			null
		);

		// When
		boolean isValid = notification.isValid();

		// Then
		assertThat(isValid).isTrue();
	}

	@Test
	void isValid_WhenTokenIsNull_ThenReturnFalse() {
		// Given
		FcmNotification notification = FcmNotification.create(
			UUID.randomUUID(),
			null,
			"Test Title",
			"Test Body",
			null
		);

		// When
		boolean isValid = notification.isValid();

		// Then
		assertThat(isValid).isFalse();
	}

	@Test
	void isValid_WhenTokenIsEmpty_ThenReturnFalse() {
		// Given
		FcmNotification notification = FcmNotification.create(
			UUID.randomUUID(),
			"",
			"Test Title",
			"Test Body",
			null
		);

		// When
		boolean isValid = notification.isValid();

		// Then
		assertThat(isValid).isFalse();
	}

	@Test
	void isValid_WhenTokenIsBlank_ThenReturnFalse() {
		// Given
		FcmNotification notification = FcmNotification.create(
			UUID.randomUUID(),
			"   ",
			"Test Title",
			"Test Body",
			null
		);

		// When
		boolean isValid = notification.isValid();

		// Then
		assertThat(isValid).isFalse();
	}

	@Test
	void isValid_WhenTitleIsNull_ThenReturnFalse() {
		// Given
		FcmNotification notification = FcmNotification.create(
			UUID.randomUUID(),
			"test-token",
			null,
			"Test Body",
			null
		);

		// When
		boolean isValid = notification.isValid();

		// Then
		assertThat(isValid).isFalse();
	}

	@Test
	void isValid_WhenTitleIsEmpty_ThenReturnFalse() {
		// Given
		FcmNotification notification = FcmNotification.create(
			UUID.randomUUID(),
			"test-token",
			"",
			"Test Body",
			null
		);

		// When
		boolean isValid = notification.isValid();

		// Then
		assertThat(isValid).isFalse();
	}

	@Test
	void isValid_WhenTitleIsBlank_ThenReturnFalse() {
		// Given
		FcmNotification notification = FcmNotification.create(
			UUID.randomUUID(),
			"test-token",
			"   ",
			"Test Body",
			null
		);

		// When
		boolean isValid = notification.isValid();

		// Then
		assertThat(isValid).isFalse();
	}

	@Test
	void isValid_WhenBodyIsNull_ThenReturnTrue() {
		// Given
		FcmNotification notification = FcmNotification.create(
			UUID.randomUUID(),
			"test-token",
			"Test Title",
			null,
			null
		);

		// When
		boolean isValid = notification.isValid();

		// Then
		assertThat(isValid).isTrue();
	}

	@Test
	void isValid_WhenDataIsNull_ThenReturnTrue() {
		// Given
		FcmNotification notification = FcmNotification.create(
			UUID.randomUUID(),
			"test-token",
			"Test Title",
			"Test Body",
			null
		);

		// When
		boolean isValid = notification.isValid();

		// Then
		assertThat(isValid).isTrue();
	}
}
