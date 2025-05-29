package com.newzet.api.article.controller.dto;

public record ArticleListResponse(
	DailyArticleResponse dailyArticleList
) {
	public static ArticleListResponse from(DailyArticleResponse dailyArticleList) {
		return new ArticleListResponse(dailyArticleList);
	}
}
