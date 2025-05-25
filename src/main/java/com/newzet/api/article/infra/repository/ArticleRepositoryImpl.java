package com.newzet.api.article.infra.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.article.business.ArticleRepository;
import com.newzet.api.article.business.dto.ArticleEntityDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepository {

	@Override
	public List<ArticleEntityDto> getMonthlyArticleWithImage(UUID userId, int year, int month) {
		return List.of();
	}

	@Override
	public ArticleEntityDto getArticleAndRead(ArticleEntityDto articleEntityDto) {
		return null;
	}
}
