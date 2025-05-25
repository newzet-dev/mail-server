package com.newzet.api.article.business;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {

	private final ArticleRepository articleRepository;

	public ArticleListResponse getArticleListAtYearAndMonth(String userId, int year, int month) {

	}

	public ArticleResponse getArticleAndRead(String articleId) {

	}

}
