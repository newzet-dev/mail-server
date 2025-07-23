package com.newzet.api.article.repository;

import java.time.LocalDateTime;
import java.util.UUID;

public record TestArticleWithImageProjection(
	UUID id,
	String fromName,
	String title,
	boolean isRead,
	LocalDateTime createdAt,
	String imageUrl
) {
}