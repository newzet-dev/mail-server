package com.newzet.api.article.business.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.article.business.batch.ArticleBatchProducer;
import com.newzet.api.article.business.repository.ArticleRepository;
import com.newzet.api.article.controller.dto.ArticleDetailResponse;
import com.newzet.api.article.controller.dto.ArticleLikeListResponse;
import com.newzet.api.article.controller.dto.ArticleListResponse;
import com.newzet.api.article.controller.dto.DailyArticleResponse;
import com.newzet.api.article.domain.Article;
import com.newzet.api.article.exception.ShareForbiddenException;
import com.newzet.api.article.repository.dto.ArticleWithImageProjection;
import com.newzet.api.common.util.UuidConverter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleService {

	private final static String ARTICLE_SHARE_PREFIX = "https://app.newzet.me/article";
	private final ArticleBatchProducer batchProducer;
	private final ArticleRepository articleRepository;

	public void saveArticleBatch(UUID userId, String fromName, String fromDomain,
		String mailingList, String imageUrl, String htmlLink, String title) {
		Article article = Article.createNewArticle(userId, fromName, fromDomain, mailingList,
			imageUrl, title, htmlLink);
		batchProducer.addToBatch(article);
	}

	public ArticleListResponse getMonthlyArticleList(UUID userId, int year, int month) {
		List<ArticleWithImageProjection> articleListAtYearAndMonth = articleRepository.getMonthlyArticleWithImage(
			userId, year, month);

		List<ArticleDetailResponse> articleList = articleListAtYearAndMonth.stream()
			.map(articleWithImageProjection -> ArticleDetailResponse.of(
				articleWithImageProjection.id(),
				articleWithImageProjection.fromName(), articleWithImageProjection.imageUrl(),
				articleWithImageProjection.title(),
				articleWithImageProjection.isRead(), articleWithImageProjection.createdAt()))
			.toList();

		return ArticleListResponse.from(getDailyArticleList(articleList));
	}

	public Article getArticle(UUID articleId) {
		Article article = articleRepository.getById(articleId).toDomain();
		if (article.checkIsUnRead()) { // isRead가 false이면 읽기 처리 수행
			Article updatedArticle = articleRepository.readArticle(article.getId()).toDomain();
			return updatedArticle;
		}
		return article;
	}

	public ArticleLikeListResponse getArticleLikeList(UUID userId) {
		List<ArticleDetailResponse> articleList = articleRepository.findLikeArticleWithImage(userId)
			.stream()
			.map(articleWithImageProjection -> ArticleDetailResponse.of(
				articleWithImageProjection.id(),
				articleWithImageProjection.fromName(), articleWithImageProjection.imageUrl(),
				articleWithImageProjection.title(),
				articleWithImageProjection.isRead(), articleWithImageProjection.createdAt()
			))
			.toList();

		return ArticleLikeListResponse.from(articleList);
	}

	@Transactional
	public void changeLikeStatus(String articleId, boolean newLikeStatus) {
		UUID convertedArticleId = UuidConverter.convert(articleId);
		articleRepository.updateLikeStatus(convertedArticleId, newLikeStatus);
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

	public Article addArticle(UUID userId, String name, String domain, String title, String url,
		String imageUrl,
		String mailingList) {
		Article article = Article.createNewArticle(userId, name, domain, mailingList, title, url,
			imageUrl);
		return articleRepository.save(article);
	}

	public Article shareArticle(UUID articleId) {
		Article article = articleRepository.getById(articleId).toDomain();
		Article sharedArticle = article.share();
		return articleRepository.save(sharedArticle);
	}

	public String getSharedUrl(UUID articleId) {
		return String.format("%s/%s", ARTICLE_SHARE_PREFIX, articleId);
	}

	public Article getSharedArticle(UUID articleId) {
		Article article = articleRepository.getById(articleId).toDomain();
		if (article.isShare()) {
			return article;
		}
		throw new ShareForbiddenException("공유가 허용되지 않은 아티클입니다.");
	}
}
