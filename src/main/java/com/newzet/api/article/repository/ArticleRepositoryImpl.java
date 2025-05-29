package com.newzet.api.article.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.article.business.ArticleRepository;
import com.newzet.api.article.repository.dto.ArticleWithImageProjection;

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
	public ArticleWithImageProjection getArticleByUserId(UUID userId) {
		return articleJpaRepository.findArticleWithImage(userId);
	}
}
