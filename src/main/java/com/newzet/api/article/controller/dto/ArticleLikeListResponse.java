package com.newzet.api.article.controller.dto;

import java.util.List;

public record ArticleLikeListResponse(
	List<ArticleDetailResponse> articleLikeList
) {

	public static ArticleLikeListResponse from(List<ArticleDetailResponse> articleLikeList) {
		return new ArticleLikeListResponse(articleLikeList);
	}
}
