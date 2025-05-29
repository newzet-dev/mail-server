package com.newzet.api.article.controller.dto;

public record ArticleDetailResponse(
	String id,
	String newsletterName,
	String newsletterImgUrl,
	String title,
	boolean isRead,
	String createdAt
) {

	public static ArticleDetailResponse of(String id, String newsletterName, String newsletterImgUrl, String title, boolean isRead, String createdAt) {
		return new ArticleDetailResponse(id, newsletterName, newsletterImgUrl, title, isRead, createdAt);
	}
}
