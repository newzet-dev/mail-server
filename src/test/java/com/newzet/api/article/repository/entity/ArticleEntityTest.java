package com.newzet.api.article.repository.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.newzet.api.article.business.dto.ArticleEntityDto;

class ArticleEntityTest {

	@Test
	void fromEntityDto_MapsAllFields() {
		// Given
		UUID id = UUID.randomUUID();
		UUID toUserId = UUID.randomUUID();
		String fromName = "Newsletter";
		String fromDomain = "example.com";
		String mailingList = "daily";
		String title = "Daily News";
		String contentUrl = "https://example.com/news";
		boolean isRead = true;
		boolean isLike = true;
		boolean isShare = false;
		LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
		LocalDateTime deletedAt = null;

		ArticleEntityDto dto = ArticleEntityDto.builder()
			.id(id)
			.toUserId(toUserId)
			.fromName(fromName)
			.fromDomain(fromDomain)
			.mailingList(mailingList)
			.title(title)
			.contentUrl(contentUrl)
			.isRead(isRead)
			.isLike(isLike)
			.isShare(isShare)
			.createdAt(createdAt)
			.deletedAt(deletedAt)
			.build();

		// When
		ArticleEntity entity = ArticleEntity.fromEntityDto(dto);

		// Then
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getToUserId()).isEqualTo(toUserId);
		assertThat(entity.getFromName()).isEqualTo(fromName);
		assertThat(entity.getFromDomain()).isEqualTo(fromDomain);
		assertThat(entity.getMailingList()).isEqualTo(mailingList);
		assertThat(entity.getTitle()).isEqualTo(title);
		assertThat(entity.getContentUrl()).isEqualTo(contentUrl);
		assertThat(entity.isRead()).isEqualTo(isRead);
		assertThat(entity.isLike()).isEqualTo(isLike);
		assertThat(entity.isShare()).isEqualTo(isShare);
		assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
		assertThat(entity.getDeletedAt()).isEqualTo(deletedAt);
	}

	@Test
	void toEntityDto_MapsAllFields() {
		// Given
		UUID id = UUID.randomUUID();
		UUID toUserId = UUID.randomUUID();
		String fromName = "Newsletter";
		String fromDomain = "example.com";
		String mailingList = "weekly";
		String title = "Weekly Digest";
		String contentUrl = "https://example.com/digest";
		boolean isRead = true;
		boolean isLike = false;
		boolean isShare = true;
		LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
		LocalDateTime deletedAt = LocalDateTime.now();

		ArticleEntity entity = ArticleEntity.builder()
			.id(id)
			.toUserId(toUserId)
			.fromName(fromName)
			.fromDomain(fromDomain)
			.mailingList(mailingList)
			.title(title)
			.contentUrl(contentUrl)
			.isRead(isRead)
			.isLike(isLike)
			.isShare(isShare)
			.createdAt(createdAt)
			.deletedAt(deletedAt)
			.build();

		// When
		ArticleEntityDto dto = entity.toEntityDto();

		// Then
		assertThat(dto.getId()).isEqualTo(id);
		assertThat(dto.getToUserId()).isEqualTo(toUserId);
		assertThat(dto.getFromName()).isEqualTo(fromName);
		assertThat(dto.getFromDomain()).isEqualTo(fromDomain);
		assertThat(dto.getMailingList()).isEqualTo(mailingList);
		assertThat(dto.getTitle()).isEqualTo(title);
		assertThat(dto.getContentUrl()).isEqualTo(contentUrl);
		assertThat(dto.isRead()).isEqualTo(isRead);
		assertThat(dto.isLike()).isEqualTo(isLike);
		assertThat(dto.isShare()).isEqualTo(isShare);
		assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
		assertThat(dto.getDeletedAt()).isEqualTo(deletedAt);
	}

	@Test
	void fromEntityDto_WhenCreatedAtIsNull_ShouldUseCurrentTime() {
		// Given
		ArticleEntityDto dto = ArticleEntityDto.builder()
			.id(UUID.randomUUID())
			.toUserId(UUID.randomUUID())
			.fromName("Test")
			.fromDomain("test.com")
			.title("Test Title")
			.contentUrl("http://test.com")
			.createdAt(null)
			.build();

		// When
		ArticleEntity entity = ArticleEntity.fromEntityDto(dto);

		// Then
		assertThat(entity.getCreatedAt()).isNotNull();
	}
}
