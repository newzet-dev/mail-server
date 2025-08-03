package com.newzet.api.article.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Article {
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

	public static Article create(
		UUID id,
		UUID toUserId,
		String fromName,
		String fromDomain,
		String mailingList,
		String title,
		String imageUrl,
		String contentUrl,
		boolean isRead,
		boolean isLike,
		boolean isShare,
		LocalDateTime createdAt,
		LocalDateTime deletedAt) {
		return Article.builder()
			.id(id)
			.toUserId(toUserId)
			.fromName(fromName)
			.fromDomain(fromDomain)
			.mailingList(mailingList)
			.title(title)
			.imageUrl(imageUrl)
			.contentUrl(contentUrl)
			.isRead(isRead)
			.isLike(isLike)
			.isShare(isShare)
			.createdAt(createdAt != null ? createdAt : LocalDateTime.now())
			.deletedAt(deletedAt)
			.build();
	}

	public static Article createNewArticle(
		UUID toUserId,
		String fromName,
		String fromDomain,
		String mailingList,
		String imageUrl,
		String title,
		String contentUrl) {
		return Article.builder()
			.toUserId(toUserId)
			.fromName(fromName)
			.fromDomain(fromDomain)
			.mailingList(mailingList)
			.imageUrl(imageUrl)
			.title(title)
			.contentUrl(contentUrl)
			.isRead(false)
			.isLike(false)
			.isShare(false)
			.createdAt(LocalDateTime.now())
			.build();
	}

	public boolean checkIsUnRead() {
		return !isRead;
	}

	public Article share() {
		return new Article(
			this.id,
			this.toUserId,
			this.fromName,
			this.fromDomain,
			this.mailingList,
			this.title,
			this.contentUrl,
			this.isRead,
			this.isLike,
			true,
			this.createdAt,
			this.deletedAt
		);
	}
}

