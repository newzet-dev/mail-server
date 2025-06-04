package com.newzet.api.article.business.repository;

import java.util.List;
import java.util.UUID;

import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.repository.dto.ArticleWithImageProjection;

public interface ArticleRepository {
	List<ArticleEntityDto> saveAll(List<ArticleEntityDto> articleEntityDtoList);

	List<ArticleWithImageProjection> getMonthlyArticleWithImage(UUID userId, int year, int month);

	ArticleEntityDto getById(UUID articleId);

	ArticleEntityDto readArticle(UUID articleId);

	List<ArticleWithImageProjection> getLikeArticleWithImage(UUID userId);

	void updateLikeStatus(UUID articleId, boolean newLikeStatus);
}
