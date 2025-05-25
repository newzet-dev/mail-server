package com.newzet.api.article.business.dto;

import java.util.UUID;

import com.newzet.api.article.domain.Article;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleEntityDto {

	private final UUID articleId;
	private final String title;
	private final String contentUrl;
	private final Boolean isRead;
	private final Boolean isLike;
	private final Boolean isShare;

	public static ArticleEntityDto create(UUID articleId, String title, String contentUrl,
		Boolean isRead, Boolean isLike, Boolean isShare) {
		return new ArticleEntityDto(articleId, title, contentUrl, isRead, isLike, isShare);
	}

	public Article toDomain() {
		return Article.create(articleId, title, contentUrl, isRead, isLike, isShare);
	}

}
