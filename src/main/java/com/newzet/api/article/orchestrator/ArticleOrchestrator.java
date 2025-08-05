package com.newzet.api.article.orchestrator;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.article.business.service.ArticleService;
import com.newzet.api.article.controller.dto.ArticleContentResponse;
import com.newzet.api.article.domain.Article;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleOrchestrator {

	private final ArticleService articleService;

	public ArticleContentResponse getSharedArticle(UUID articleId) {
		Article sharedArticle = articleService.getSharedArticle(articleId);
		String articleContent = articleService.getContentUrl();
		return new ArticleContentResponse(sharedArticle.getTitle(), articleContent, sharedArticle.isLike());
	}
}
