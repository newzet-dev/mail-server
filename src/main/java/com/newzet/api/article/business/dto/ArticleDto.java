package com.newzet.api.article.business.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.newzet.api.article.domain.Article;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ArticleDto {
	private final UUID id;
	private final UUID toUserId;
	private final String fromName;
	private final String fromDomain;
	private final String mailingList;
	private final String title;
	private final String contentUrl;
	private final boolean isRead;
	private final boolean isLike;
	private final boolean isShare;
	private final LocalDateTime createdAt;

	public static ArticleDto from(Article article) {
		return ArticleDto.builder()
			.id(article.getId())
			.toUserId(article.getToUserId())
			.fromName(article.getFromName())
			.fromDomain(article.getFromDomain())
			.mailingList(article.getMailingList())
			.title(article.getTitle())
			.contentUrl(article.getContentUrl())
			.isRead(article.isRead())
			.isLike(article.isLike())
			.isShare(article.isShare())
			.createdAt(article.getCreatedAt())
			.build();
	}

	public Article toDomain() {
		return Article.createNewArticle(
			toUserId,
			fromName,
			fromDomain,
			mailingList,
			title,
			contentUrl
		);
	}
}
