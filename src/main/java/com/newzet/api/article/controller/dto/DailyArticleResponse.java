package com.newzet.api.article.controller.dto;

import java.util.List;

public record DailyArticleResponse(
	int day,
	List<ArticleDetailResponse> articleList
) {

	public static DailyArticleResponse of(int day, List<ArticleDetailResponse> articleList) {
		return new DailyArticleResponse(day, articleList);
	}
}
