package com.newzet.api.newsletter.fixture;

import java.util.UUID;

import com.newzet.api.category.domain.Category;
import com.newzet.api.category.repository.CategoryEntity;
import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;
import com.newzet.api.newsletter.domain.model.Color;
import com.newzet.api.newsletter.domain.model.Newsletter;
import com.newzet.api.newsletter.repository.NewsletterEntity;

public class NewsletterFixture {

	private static final String name = "test";
	private static final String domain = "test@example.com";
	private static final String mailingList = "test123";

	public static Newsletter createDefaultDomain() {
		return Newsletter.create(UUID.randomUUID(), name,
			Category.create(UUID.randomUUID(), "test", "test", "test"), domain,
			mailingList, 1, "test", "test", "test", "test", "test", "test", Color.DEFAULT);
	}

	public static NewsletterEntity createDefaultEntity() {
		return NewsletterEntity.create(name,
			CategoryEntity.create(UUID.randomUUID(), "test", "test", "test"), domain,
			mailingList, 1, "test", "test", "test", "test", "test", "test", Color.DEFAULT);
	}

	public static NewsletterEntity createEntityWithId() {
		return NewsletterEntity.create(UUID.randomUUID(), name,
			CategoryEntity.create(UUID.randomUUID(), "test", "test", "test"), domain,
			mailingList, 1, "test", "test", "test", "test", "test", "test", Color.DEFAULT, null);
	}

	public static NewsletterEntity createDefaultEntity(CategoryEntity categoryEntity) {
		return NewsletterEntity.create(name, categoryEntity, domain,
			mailingList, 1, "test", "test", "test", "test", "test", "test", Color.DEFAULT);
	}

	public static NewsletterEntity createEntityUnique(String domain, CategoryEntity categoryEntity) {
		return NewsletterEntity.create(name, categoryEntity, domain,
			mailingList, 1, "test", "test", "test", "test", "test", "test", Color.DEFAULT);
	}

	public static NewsletterCacheDto createDefaultCacheDto(CategoryEntity categoryEntity) {
		return NewsletterCacheDto.create(UUID.randomUUID(), name, categoryEntity.toEntityDto()
				.toDomain(), domain,
			mailingList, 1, "test", "test", "test", "test", "test", "test", Color.DEFAULT);
	}
}
