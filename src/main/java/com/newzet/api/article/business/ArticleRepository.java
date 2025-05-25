package com.newzet.api.article.business;

import java.util.List;
import java.util.UUID;

public interface ArticleRepository {

	List<ArticleEntityDto> getArticleListAtYearAndMonth(UUID userId, int year, int month);

	ArticleEntityDto getById(UUID articleId);
}
