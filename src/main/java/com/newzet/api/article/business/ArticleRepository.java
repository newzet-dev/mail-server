package com.newzet.api.article.business;

import java.util.List;
import java.util.UUID;

import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.repository.dto.ArticleWithImageProjection;

public interface ArticleRepository {

	List<ArticleWithImageProjection> getMonthlyArticleWithImage(UUID userId, int year, int month);

	ArticleEntityDto getById(UUID articleId);

	void readArticle(UUID articleId);

	ArticleWithImageProjection getArticleByUserId(UUID userId);
}
