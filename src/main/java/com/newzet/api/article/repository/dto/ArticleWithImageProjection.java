package com.newzet.api.article.repository.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ArticleWithImageProjection {
	UUID getId();

	String getFromName();

	String getTitle();

	Boolean getIsRead();

	LocalDateTime getCreatedAt();

	String getImageUrl();

}
