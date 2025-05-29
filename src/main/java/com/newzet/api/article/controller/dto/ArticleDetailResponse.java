package com.newzet.api.article.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ArticleDetailResponse(
	String id,
	String newsletterName,
	String newsletterImgUrl,
	String title,
	boolean isRead,
	String createdAt
) {

	public static ArticleDetailResponse of(UUID id, String newsletterName, String newsletterImgUrl, String title, boolean isRead, LocalDateTime createdAt) {
		return new ArticleDetailResponse(id.toString(), newsletterName, newsletterImgUrl, title, isRead, createdAt.toString());
	}

	public int getDay() {
		return LocalDateTime.parse(createdAt).getDayOfMonth();
	}
}
