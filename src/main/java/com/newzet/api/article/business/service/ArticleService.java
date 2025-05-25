package com.newzet.api.article.business.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.article.business.dto.ArticleDto;
import com.newzet.api.article.domain.Article;
import com.newzet.api.common.batch.BatchProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

	private final BatchProcessor batchProcessor;

	public void saveArticleBatch(UUID userId, String fromName, String fromDomain,
		String mailingList, String htmlLink, String title) {
		Article article = Article.createNewArticle(userId, fromName, fromDomain, mailingList, title,
			htmlLink);
		ArticleDto articleDto = ArticleDto.from(article);
		batchProcessor.addToBatch(articleDto);
	}
}
