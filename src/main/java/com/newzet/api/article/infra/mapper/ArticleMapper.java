package com.newzet.api.article.infra.mapper;

import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;

import com.newzet.api.article.infra.dto.ArticleWithImageResponse;

@Mapper
public interface ArticleMapper {
	List<ArticleWithImageResponse> selectMonthlyArticleWithImage(UUID userId, int year, int month);
}
