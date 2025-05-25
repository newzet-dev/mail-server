package com.newzet.api.article.business.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.newzet.api.article.domain.Article;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ArticleEntityDto {

	private final UUID articleId;
	private final String toUserId;
	private final String fromName;
	private final String fromDomain;
	private final String mailingList;
	private final String title;
	private final String contentUrl;
	private final Boolean isRead;
	private final Boolean isLike;
	private final Boolean isShare;
	private final LocalDateTime createdAt;
	private final LocalDateTime deletedAt;

	public static ArticleEntityDto create(UUID articleId, String toUserId, String fromName,
		String fromDomain, String mailingList, String title, String contentUrl,
		Boolean isRead, Boolean isLike, Boolean isShare, LocalDateTime createdAt,
		LocalDateTime deletedAt) {
		return new ArticleEntityDto(articleId, toUserId, fromName, fromDomain, mailingList, title,
			contentUrl, isRead, isLike, isShare, createdAt, deletedAt);
	}

	public Article toDomain() {
		return Article.create(articleId, title, contentUrl, isRead, isLike, isShare);
	}

}
