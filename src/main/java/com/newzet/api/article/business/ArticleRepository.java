package com.newzet.api.article.business;

import java.util.List;
import java.util.UUID;

import com.newzet.api.article.business.dto.ArticleEntityDto;

public interface ArticleRepository {

	List<ArticleEntityDto> getMonthlyArticleWithImage(UUID userId, int year, int month);

	ArticleEntityDto getArticleAndRead(ArticleEntityDto articleEntityDto);
}
