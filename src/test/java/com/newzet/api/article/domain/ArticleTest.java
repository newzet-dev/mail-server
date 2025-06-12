package com.newzet.api.article.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ArticleTest {

	@Test
	void createNewArticle_SetsDefaultValues() {
		// Given
		UUID toUserId = UUID.randomUUID();
		String fromName = "Newsletter";
		String fromDomain = "example.com";
		String mailingList = "daily-news";
		String title = "Today's News";
		String contentUrl = "https://example.com/news/1";

		// When
		Article article = Article.createNewArticle(
			toUserId, fromName, fromDomain, mailingList, title, contentUrl);

		// Then
		assertThat(article.getId()).isNull();
		assertThat(article.getToUserId()).isEqualTo(toUserId);
		assertThat(article.getFromName()).isEqualTo(fromName);
		assertThat(article.getFromDomain()).isEqualTo(fromDomain);
		assertThat(article.getMailingList()).isEqualTo(mailingList);
		assertThat(article.getTitle()).isEqualTo(title);
		assertThat(article.getContentUrl()).isEqualTo(contentUrl);
		assertThat(article.isRead()).isFalse();
		assertThat(article.isLike()).isFalse();
		assertThat(article.isShare()).isFalse();
		assertThat(article.getCreatedAt()).isNotNull();
		assertThat(article.getDeletedAt()).isNull();
	}

	@Test
	void create_WithAllParameters() {
		// Given
		UUID id = UUID.randomUUID();
		UUID toUserId = UUID.randomUUID();
		String fromName = "Newsletter";
		String fromDomain = "example.com";
		String mailingList = "weekly-digest";
		String title = "This Week's Digest";
		String imageUrl = "https://example.com/image.jpg";
		String contentUrl = "https://example.com/digest/1";
		boolean isRead = true;
		boolean isLike = true;
		boolean isShare = false;
		LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
		LocalDateTime deletedAt = LocalDateTime.now();

		// When
		Article article = Article.create(
			id, toUserId, fromName, fromDomain, mailingList,
			title, imageUrl, contentUrl, isRead, isLike, isShare, createdAt, deletedAt);

		// Then
		assertThat(article.getId()).isEqualTo(id);
		assertThat(article.getToUserId()).isEqualTo(toUserId);
		assertThat(article.getFromName()).isEqualTo(fromName);
		assertThat(article.getFromDomain()).isEqualTo(fromDomain);
		assertThat(article.getMailingList()).isEqualTo(mailingList);
		assertThat(article.getTitle()).isEqualTo(title);
		assertThat(article.getContentUrl()).isEqualTo(contentUrl);
		assertThat(article.isRead()).isEqualTo(isRead);
		assertThat(article.isLike()).isEqualTo(isLike);
		assertThat(article.isShare()).isEqualTo(isShare);
		assertThat(article.getCreatedAt()).isEqualTo(createdAt);
		assertThat(article.getDeletedAt()).isEqualTo(deletedAt);
	}

	@Test
	void create_WithNullCreatedAt_ShouldUseCurrentTime() {
		// Given
		UUID id = UUID.randomUUID();
		UUID toUserId = UUID.randomUUID();

		// When
		Article article = Article.create(
			id, toUserId, "Newsletter", "example.com", "daily",
			"Title", "url", "url", false, false, false, null, null);

		// Then
		assertThat(article.getCreatedAt()).isNotNull();
	}
}
