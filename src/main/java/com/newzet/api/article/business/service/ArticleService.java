package com.newzet.api.article.business.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.article.business.repository.ArticleRepository;
import com.newzet.api.article.controller.dto.ArticleContentResponse;
import com.newzet.api.article.controller.dto.ArticleDetailResponse;
import com.newzet.api.article.controller.dto.ArticleLikeListResponse;
import com.newzet.api.article.controller.dto.ArticleListResponse;
import com.newzet.api.article.controller.dto.DailyArticleResponse;
import com.newzet.api.article.domain.Article;
import com.newzet.api.article.repository.dto.ArticleWithImageProjection;
import com.newzet.api.common.batch.BatchProducer;
import com.newzet.api.common.util.UuidConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

	private final BatchProducer batchProducer;
	private final ArticleRepository articleRepository;

	public void saveArticleBatch(UUID userId, String fromName, String fromDomain,
		String mailingList, String htmlLink, String title) {
		Article article = Article.createNewArticle(userId, fromName, fromDomain, mailingList, title,
			htmlLink);
		batchProducer.addToBatch(article);
	}

	public ArticleListResponse getMonthlyArticleList(UUID userId, int year, int month) {
		List<ArticleWithImageProjection> articleListAtYearAndMonth = articleRepository.getMonthlyArticleWithImage(
			userId, year, month);

		List<ArticleDetailResponse> articleList = articleListAtYearAndMonth.stream()
			.map(articleWithImageProjection -> ArticleDetailResponse.of(
				articleWithImageProjection.getId(),
				articleWithImageProjection.getFromName(), articleWithImageProjection.getImageUrl(),
				articleWithImageProjection.getTitle(),
				articleWithImageProjection.getIsRead(), articleWithImageProjection.getCreatedAt()))
			.toList();

		return ArticleListResponse.from(getDailyArticleList(articleList));
	}

	@Transactional
	public ArticleContentResponse getArticle(String articleId) {
		UUID convertedArticleId = UuidConverter.convert(articleId);
		Article article = articleRepository.getById(convertedArticleId).toDomain();

		if (article.checkIsUnRead()) { // isRead가 false이면 읽기 처리 수행
			Article updatedArticle = articleRepository.readArticle(article.getId()).toDomain();
			return ArticleContentResponse.of(updatedArticle.getTitle(),
				updatedArticle.getContentUrl(), updatedArticle.isRead());
		}

		return ArticleContentResponse.of(article.getTitle(), article.getContentUrl(),
			article.isLike());
	}

	public ArticleLikeListResponse getArticleLikeList(UUID userId) {
		List<ArticleDetailResponse> articleList = articleRepository.getLikeArticleWithImage(userId)
			.stream()
			.map(articleWithImageProjection -> ArticleDetailResponse.of(
				articleWithImageProjection.getId(),
				articleWithImageProjection.getFromName(), articleWithImageProjection.getImageUrl(),
				articleWithImageProjection.getTitle(),
				articleWithImageProjection.getIsRead(), articleWithImageProjection.getCreatedAt()
			))
			.toList();

		return ArticleLikeListResponse.from(articleList);
	}

	// 반환된 dto의 정렬된 순서를 유지하면서, day 별로 DailyArticleResponse를 묶음
	private List<DailyArticleResponse> getDailyArticleList(
		List<ArticleDetailResponse> articleDetailResponseList) {
		return articleDetailResponseList.stream()
			.collect(Collectors.groupingBy(
				ArticleDetailResponse::getDay,
				LinkedHashMap::new, // 입력 순서 유지
				Collectors.toList()
			))
			.entrySet().stream()
			.map(entry -> DailyArticleResponse.of(entry.getKey(), entry.getValue()))
			.toList();
	}
}
