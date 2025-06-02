package com.newzet.api.article.business.repository;

import java.util.List;

import com.newzet.api.article.business.dto.ArticleEntityDto;

public interface ArticleRepository {
	List<ArticleEntityDto> saveAll(List<ArticleEntityDto> articleEntityDtoList);
}
