package com.newzet.api.fcm.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FcmToken {
	private final UUID id;
	private final LocalDateTime createdAt;
	private final UUID userId;
	private final String value;

	public static FcmToken create(UUID userId, String value) {
		return new FcmToken(null, LocalDateTime.now(), userId, value);
	}

	public FcmToken changeUserId(UUID userId) {
		return new FcmToken(this.id, this.createdAt, userId, this.value);
	}
}
