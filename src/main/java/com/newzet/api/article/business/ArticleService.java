package com.newzet.api.article.business;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.article.controller.dto.ArticleContentResponse;
import com.newzet.api.article.controller.dto.ArticleDetailResponse;
import com.newzet.api.article.controller.dto.ArticleListResponse;
import com.newzet.api.article.controller.dto.DailyArticleResponse;
import com.newzet.api.article.domain.Article;
import com.newzet.api.article.repository.dto.ArticleWithImageProjection;
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

		List<ArticleDetailResponse> articleList = articleListAtYearAndMonth.stream()
			.map(articleWithImageProjection -> ArticleDetailResponse.of(
				articleWithImageProjection.getArticleId(),
				articleWithImageProjection.getFromName(), articleWithImageProjection.getImageUrl(),
				articleWithImageProjection.getTitle(),
				articleWithImageProjection.getIsRead(), articleWithImageProjection.getCreatedAt()))
			.toList();

		return ArticleListResponse.from(DailyArticleResponse.of(31, articleList));
	}

	public ArticleContentResponse getArticle(String articleId) {
		UUID convertArticleId = UuidConverter.convert(articleId);
		Article article = articleRepository.getById(convertArticleId).toDomain();
		if (article.checkIsUnRead()) {
			Article updatedArticle = article.readArticle();
			articleRepository.readArticle(updatedArticle.getId());
			return ArticleContentResponse.of(updatedArticle.getTitle(),
				updatedArticle.getContentUrl(), updatedArticle.isLike());
		}

		return ArticleContentResponse.of(article.getTitle(), article.getContentUrl(),
			article.isLike());
	}

}
