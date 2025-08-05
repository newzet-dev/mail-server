package com.newzet.api.article.repository.mapper;

import com.newzet.api.article.domain.Article;
import com.newzet.api.article.repository.entity.ArticleEntity;

public class ArticleEntityMapper {
	public static Article toDomain(ArticleEntity entity) {
		return Article.create(entity.getId(), entity.getToUserId(), entity.getFromName(), entity.getFromDomain(),
			entity.getMailingList(), entity.getTitle(), entity.getImageUrl(), entity.getContentUrl(), entity.isRead(),
			entity.isLike(), entity.isShare(), entity.getCreatedAt(), entity.getDeletedAt());
	}

	public static ArticleEntity toEntity(Article domain) {
		return new ArticleEntity(domain.getId(), domain.getToUserId(), domain.getFromName(), domain.getFromDomain(),
			domain.getMailingList(), domain.getTitle(), domain.getImageUrl(), domain.getContentUrl(), domain.isRead(),
			domain.isLike(), domain.isShare(), domain.getCreatedAt(), domain.getDeletedAt());
	}
}
