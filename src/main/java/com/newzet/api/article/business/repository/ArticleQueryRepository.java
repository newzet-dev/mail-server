package com.newzet.api.article.business.repository;

import java.util.List;
import java.util.UUID;

import com.newzet.api.article.repository.dto.ArticleWithImageProjection;

public interface ArticleQueryRepository {
	List<ArticleWithImageProjection> findMonthlyArticlesWithImage(UUID userId, int year, int month);

	List<ArticleWithImageProjection> findLikeArticleWithImage(UUID userId);
}
