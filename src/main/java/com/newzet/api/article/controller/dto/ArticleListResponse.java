package com.newzet.api.article.controller.dto;

import java.util.List;

public record ArticleListResponse(
	List<DailyArticleResponse> dailyArticleList
) {
	public static ArticleListResponse from(List<DailyArticleResponse> dailyArticleList) {
		return new ArticleListResponse(dailyArticleList);
	}
}
