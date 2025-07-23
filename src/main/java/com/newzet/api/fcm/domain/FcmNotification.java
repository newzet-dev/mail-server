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
	private UUID articleId;
	private LocalDateTime articleCreatedAt;
	private String articleTitle;
	private String newsletterName;

	public static FcmNotification create(UUID userId, String token, UUID articleId,
		LocalDateTime articleCreatedAt,
		String articleTitle, String newsletterName) {
		return new FcmNotification(
			userId,
			token,
			articleId,
			articleCreatedAt,
			articleTitle,
			newsletterName);
	}

	public boolean isValid() {
		return token != null && !token.trim().isEmpty()
			&& articleTitle != null && !articleTitle.trim().isEmpty();
	}
}
