package com.newzet.api.article.business;

import java.util.List;
import java.util.UUID;

import com.newzet.api.article.infra.dto.ArticleWithImageProjection;

public interface ArticleRepository {

	List<ArticleWithImageProjection> getMonthlyArticleWithImage(UUID userId, int year, int month);

	ArticleWithImageProjection getArticleByUserId(UUID userId);
}
