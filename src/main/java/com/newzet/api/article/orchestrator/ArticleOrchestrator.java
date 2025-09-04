package com.newzet.api.article.orchestrator;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.article.business.service.ArticleService;
import com.newzet.api.article.controller.dto.ArticleContentResponse;
import com.newzet.api.article.domain.Article;
import com.newzet.api.common.s3.S3Service;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleOrchestrator {

	private final ArticleService articleService;
	private final S3Service s3Service;

	public ArticleContentResponse getSharedArticle(UUID articleId) {
		Article sharedArticle = articleService.getSharedArticle(articleId);
		String content = s3Service.getContentAsString(sharedArticle.getContentUrl());
		return new ArticleContentResponse(sharedArticle.getTitle(), content,
			sharedArticle.isLike());
	}

	@Transactional
	public ArticleContentResponse getArticle(UUID articleId) {
		Article article = articleService.getArticle(articleId);
		String content = s3Service.getContentAsString(article.getContentUrl());
		return ArticleContentResponse.of(article.getTitle(), content,
			article.isLike());
	}
}
