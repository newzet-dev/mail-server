package com.newzet.api.fcm.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record FcmToken(
	UUID id,
	LocalDateTime createdAt,
	UUID userId,
	String value) {

	public static FcmToken create(UUID userId, String value) {
		return new FcmToken(null, LocalDateTime.now(), userId, value);
	}

	public FcmToken changeUserId(UUID userId) {
		return new FcmToken(this.id, this.createdAt, userId, this.value);
	}
}
