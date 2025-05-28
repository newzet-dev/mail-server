package com.newzet.api.article.business;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.article.infra.dto.ArticleWithImageProjection;
import com.newzet.api.article.presentation.dto.ArticleDetailResponse;
import com.newzet.api.article.presentation.dto.ArticleListResponse;
import com.newzet.api.common.util.UuidConverter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {

	private final ArticleRepository articleRepository;

	public ArticleListResponse getMonthlyArticleList(String userId, int year, int month) {
		UUID convertUserId = UuidConverter.convert(userId);
		List<ArticleWithImageProjection> articleListAtYearAndMonth = articleRepository.getMonthlyArticleWithImage(
			convertUserId, year, month);
		return null;
	}

	public ArticleDetailResponse getArticle(String articleId) {
		return null;
	}

}
