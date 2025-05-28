package com.newzet.api.article.infra.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.article.business.ArticleRepository;
import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.infra.dto.ArticleWithImageProjection;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepository {

	private final ArticleJpaRepository articleJpaRepository;

	@Override
	public List<ArticleWithImageProjection> getMonthlyArticleWithImage(UUID userId, int year, int month) {
		return articleJpaRepository.findMonthlyArticlesWithImage(
			userId, year, month);
	}

	@Override
	public ArticleEntityDto getArticleAndRead(ArticleEntityDto articleEntityDto) {
		return null;
	}
}
