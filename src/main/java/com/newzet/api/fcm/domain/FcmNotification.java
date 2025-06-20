package com.newzet.api.fcm.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FcmNotification {
	private UUID userId;
	private String token;
	private String title;
	private String body;
	private String data;
	private LocalDateTime createdAt;

	public static FcmNotification create(UUID userId, String token, String title, String body,
		String data) {
		return new FcmNotification(
			userId,
			token,
			title,
			body,
			data,
			LocalDateTime.now()
		);
	}

	public boolean isValid() {
		return token != null && !token.trim().isEmpty()
			&& title != null && !title.trim().isEmpty();
	}
}
