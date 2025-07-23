package com.newzet.api.article.repository.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ArticleWithImageProjection(
	UUID id,
	String fromName,
	String title,
	boolean isRead,
	LocalDateTime createdAt,
	String imageUrl
) {
}
