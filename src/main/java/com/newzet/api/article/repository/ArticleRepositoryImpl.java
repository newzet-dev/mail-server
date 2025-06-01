package com.newzet.api.article.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.article.business.ArticleRepository;
import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.repository.dto.ArticleWithImageProjection;
import com.newzet.api.article.repository.entity.ArticleEntity;
import com.newzet.api.article.repository.exception.NoArticleException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepository {

	private final ArticleJpaRepository articleJpaRepository;

	@Override
	public List<ArticleWithImageProjection> getMonthlyArticleWithImage(UUID userId, int year,
		int month) {
		return articleJpaRepository.findMonthlyArticlesWithImage(
			userId, year, month);
	}

	@Override
	public ArticleEntityDto getById(UUID articleId) {
		return articleJpaRepository.findById(articleId)
			.orElseThrow(() -> new NoArticleException("해당 id의 아티클이 존재하지 않습니다."))
			.toEntityDto();
	}

	@Override
	public ArticleEntityDto readArticle(UUID articleId) {
		ArticleEntity articleEntity = articleJpaRepository.findById(articleId)
			.orElseThrow(() -> new NoArticleException("해당 id의 아티클이 존재하지 않습니다."));

		articleEntity.readArticle();
		return articleEntity.toEntityDto();
	}

	@Override
	public ArticleWithImageProjection getArticleByUserId(UUID userId) {
		return articleJpaRepository.findArticleWithImage(userId);
	}
}
