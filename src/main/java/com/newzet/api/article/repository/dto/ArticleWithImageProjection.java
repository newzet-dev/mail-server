package com.newzet.api.article.repository.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ArticleWithImageProjection {
	private UUID id;
	private String fromName;
	private String title;
	private Boolean isRead;
	private LocalDateTime createdAt;
	private String imageUrl;


}
