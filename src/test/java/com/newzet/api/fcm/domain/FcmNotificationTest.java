package com.newzet.api.fcm.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class FcmNotificationTest {

	@Test
	void create_WhenValidParameters_ThenCreateNotification() {
		// Given
		UUID userId = UUID.randomUUID();
		String token = "test-fcm-token";
		UUID articleId = UUID.randomUUID();
		LocalDateTime articleCreatedAt = LocalDateTime.now();
		String articleTitle = "New Article";
		String newsletterName = "Test Newsletter";

		// When
		FcmNotification notification = FcmNotification.create(userId, token, articleId,
			articleCreatedAt, articleTitle, newsletterName);

		// Then
		assertThat(notification.getUserId()).isEqualTo(userId);
		assertThat(notification.getToken()).isEqualTo(token);
		assertThat(notification.getArticleId()).isEqualTo(articleId);
		assertThat(notification.getArticleCreatedAt()).isEqualTo(articleCreatedAt);
		assertThat(notification.getArticleTitle()).isEqualTo(articleTitle);
		assertThat(notification.getNewsletterName()).isEqualTo(newsletterName);
	}

	@Test
	void isValid_WhenTokenAndTitleNotEmpty_ThenReturnTrue() {
		// Given
		FcmNotification notification = FcmNotification.create(
			UUID.randomUUID(),
			"test-token",
			UUID.randomUUID(),
			LocalDateTime.now(),
			"Test Title",
			"Test Newsletter"
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
			UUID.randomUUID(),
			LocalDateTime.now(),
			"Test Title",
			"Test Newsletter"
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
			UUID.randomUUID(),
			LocalDateTime.now(),
			"Test Title",
			"Test Newsletter"
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
			UUID.randomUUID(),
			LocalDateTime.now(),
			"Test Title",
			"Test Newsletter"
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
			UUID.randomUUID(),
			LocalDateTime.now(),
			null,
			"Test Newsletter"
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
			UUID.randomUUID(),
			LocalDateTime.now(),
			"",
			"Test Newsletter"
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
			UUID.randomUUID(),
			LocalDateTime.now(),
			"   ",
			"Test Newsletter"
		);

		// When
		boolean isValid = notification.isValid();

		// Then
		assertThat(isValid).isFalse();
	}

	@Test
	void isValid_WhenNewsletterNameIsNull_ThenReturnTrue() {
		// Given
		FcmNotification notification = FcmNotification.create(
			UUID.randomUUID(),
			"test-token",
			UUID.randomUUID(),
			LocalDateTime.now(),
			"Test Title",
			null
		);

		// When
		boolean isValid = notification.isValid();

		// Then
		assertThat(isValid).isTrue();
	}

	@Test
	void isValid_WhenArticleIdIsNull_ThenReturnTrue() {
		// Given
		FcmNotification notification = FcmNotification.create(
			UUID.randomUUID(),
			"test-token",
			null,
			LocalDateTime.now(),
			"Test Title",
			"Test Newsletter"
		);

		// When
		boolean isValid = notification.isValid();

		// Then
		assertThat(isValid).isTrue();
	}
}
