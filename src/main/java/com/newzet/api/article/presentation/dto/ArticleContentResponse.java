package com.newzet.api.article.presentation.dto;

public record ArticleContentResponse(
	String title,
	String content,
	boolean isLike
) {

	public static ArticleContentResponse of(String title, String content, boolean isLike) {
		return new ArticleContentResponse(title, content, isLike);
	}
}
