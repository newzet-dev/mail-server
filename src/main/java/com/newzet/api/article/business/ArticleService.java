package com.newzet.api.article.business;

import static java.util.Map.Entry.*;
import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(readOnly = true)
public class ArticleService {

	private final ArticleRepository articleRepository;

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

	private List<DailyArticleResponse> getDailyArticleList(
		List<ArticleDetailResponse> articleDetailResponseList) {
		// 일별로 아티클 모으기(key: day, value: Article List)
		List<Entry<Integer, List<ArticleDetailResponse>>> articleSubListSortedByDay =
			articleDetailResponseList.stream()
				.collect(groupingBy(ArticleDetailResponse::getDay))
				.entrySet().stream()
				.sorted(comparingByKey())
				.toList();

		return articleSubListSortedByDay.stream()
			.map(articleSubList ->
				DailyArticleResponse.of(articleSubList.getKey(),
					articleSubList.getValue()))
			.toList();
	}

}
