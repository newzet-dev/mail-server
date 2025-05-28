package com.newzet.api.article.infra.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ArticleWithImageProjection {
	private UUID articleId;
	private String fromName;
	private String title;
	private Boolean isRead;
	private LocalDateTime createdAt;
	private String imageUrl;


}
