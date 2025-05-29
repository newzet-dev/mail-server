package com.newzet.api.article.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.controller.dto.ArticleContentResponse;
import com.newzet.api.article.domain.Article;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

	@Mock
	private ArticleRepository articleRepository;
	@InjectMocks
	private ArticleService articleService;

	@DisplayName("아티클을 처음 조회하는 경우에 조회 처리가 된다.")
	@Test
	void read_article_in_first_time() {
		// Given
		Article article = Article.create(UUID.randomUUID(), UUID.randomUUID(), "newsletter name",
			"domain",
			"mail-list", "title", "https://", false, false, false, LocalDateTime.now(),
			LocalDateTime.now());
		String articleId = article.getId().toString();
		when(articleRepository.getById(any(UUID.class)))
			.thenReturn(ArticleEntityDto.fromDomain(article));
		doNothing().when(articleRepository).readArticle(any(UUID.class));

		// When
		assertFalse(article.isRead());
		ArticleContentResponse articleResponse = articleService.getArticle(articleId);

		// Then
		assertTrue(articleResponse.isLike());
	}

	@DisplayName("아티클을 처음 조회하는 것이 아닌 경우에 조회 처리를 하지 않는다.")
	@Test
	void read_article_not_in_first_time() {
		// Given
		Article article = Article.create(UUID.randomUUID(), UUID.randomUUID(), "newsletter name",
			"domain",
			"mail-list", "title", "https://", true, false, false, LocalDateTime.now(),
			LocalDateTime.now());
		String articleId = article.getId().toString();
		when(articleRepository.getById(any(UUID.class)))
			.thenReturn(ArticleEntityDto.fromDomain(article));

		// When
		assertTrue(article.isRead());
		ArticleContentResponse articleResponse = articleService.getArticle(articleId);

		// Then
		verify(articleRepository, never()).readArticle(any(UUID.class));
	}
}