package com.newzet.api.article.business.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.newzet.api.article.domain.Article;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ArticleEntityDto {
	private final UUID id;
	private final UUID toUserId;
	private final String fromName;
	private final String fromDomain;
	private final String mailingList;
	private final String title;
	private final String imageUrl;
	private final String contentUrl;
	private final boolean isRead;
	private final boolean isLike;
	private final boolean isShare;
	private final LocalDateTime createdAt;
	private final LocalDateTime deletedAt;

	public static ArticleEntityDto fromDomain(Article article) {
		return ArticleEntityDto.builder()
			.id(article.getId())
			.toUserId(article.getToUserId())
			.fromName(article.getFromName())
			.fromDomain(article.getFromDomain())
			.mailingList(article.getMailingList())
			.title(article.getTitle())
			.imageUrl(article.getImageUrl())
			.contentUrl(article.getContentUrl())
			.isRead(article.isRead())
			.isLike(article.isLike())
			.isShare(article.isShare())
			.createdAt(article.getCreatedAt())
			.deletedAt(article.getDeletedAt())
			.build();
	}

	public Article toDomain() {
		return Article.create(
			id,
			toUserId,
			fromName,
			fromDomain,
			mailingList,
			title,
			imageUrl,
			contentUrl,
			isRead,
			isLike,
			isShare,
			createdAt,
			deletedAt
		);
	}
}