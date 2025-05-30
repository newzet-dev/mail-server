package com.newzet.api.article.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import com.newzet.api.article.repository.dto.ArticleWithImageProjection;

public class TestArticleWithImageProjection implements ArticleWithImageProjection {
	private final UUID id;
	private final String fromName;
	private final String title;
	private final Boolean isRead;
	private final LocalDateTime createdAt;
	private final String imageUrl;

	public TestArticleWithImageProjection(UUID id, String fromName, String title,
		Boolean isRead, LocalDateTime createdAt, String imageUrl) {
		this.id = id;
		this.fromName = fromName;
		this.title = title;
		this.isRead = isRead;
		this.createdAt = createdAt;
		this.imageUrl = imageUrl;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public String getFromName() {
		return fromName;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Boolean getIsRead() {
		return isRead;
	}

	@Override
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	@Override
	public String getImageUrl() {
		return imageUrl;
	}
}