package com.newzet.api.article.domain;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Article {

	private final UUID articleId;
	private final String title;
	private final String contentUrl;
	private final Boolean isRead;
	private final Boolean isLike;
	private final Boolean isShare;

	public static Article create(UUID articleId, String title, String contentUrl, Boolean isRead,
		Boolean isLike, Boolean isShare) {
		return new Article(articleId, title, contentUrl, isRead, isLike, isShare);
	}
}
