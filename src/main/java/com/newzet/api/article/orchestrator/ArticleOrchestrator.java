package com.newzet.api.article.orchestrator;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.article.business.service.ArticleService;
import com.newzet.api.article.controller.dto.ArticleContentResponse;
import com.newzet.api.article.domain.Article;
import com.newzet.api.common.s3.S3Service;
import com.newzet.api.config.s3.S3Config;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleOrchestrator {

	private final ArticleService articleService;
	private final S3Service s3Service;
	private final S3Config s3Config;

	public ArticleContentResponse getSharedArticle(UUID articleId) {
		Article sharedArticle = articleService.getSharedArticle(articleId);
		String articleContent = articleService.getContentUrl();
		return new ArticleContentResponse(sharedArticle.getTitle(), articleContent,
			sharedArticle.isLike());
	}

	public ArticleContentResponse getArticle(UUID articleId) {
		Article article = articleService.getArticle(articleId);
		// S3로부터 본문 HTML를 받아옴
		// contentUrl.html 형태로 접근해서 가져와, content 필드에 주입
		String content = getContentFromS3(article.getContentUrl());

		return ArticleContentResponse.of(article.getTitle(), content,
			article.isLike());
	}

	private String getContentFromS3(String key) {
		return s3Service.getContentAsString(s3Config.getContentBucketName(), key);
	}
}
