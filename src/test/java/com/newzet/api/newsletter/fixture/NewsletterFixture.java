package com.newzet.api.newsletter.fixture;

import java.util.UUID;

import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.domain.Color;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.repository.NewsletterEntity;

public class NewsletterFixture {

	private static final String name = "test";
	private static final String domain = "test@example.com";
	private static final String mailingList = "test123";
	private static final String categoryName = "testCategory";

	public static Newsletter createDefaultDomain() {
		return Newsletter.create(UUID.randomUUID(), name, categoryName, domain,
			mailingList,1,"test","test","test","test","test","test", Color.DEFAULT);
	}

	public static NewsletterEntityDto createDtoByEntity(NewsletterEntity entity) {
		return entity.toEntityDto();
	}

	public static NewsletterEntity createDefaultEntity() {
		return NewsletterEntity.create(name, categoryName, domain,
			mailingList,1,"test","test","test","test","test","test", Color.DEFAULT);
	}

	public static NewsletterEntityDto createDefaultDto() {
		return NewsletterEntityDto.create(UUID.randomUUID(), name, categoryName, domain,
			mailingList,1,"test","test","test","test","test","test", Color.DEFAULT);
	}

	public static NewsletterCacheDto createDefaultCacheDto() {
		return NewsletterCacheDto.create(UUID.randomUUID(), name, categoryName, domain,
			mailingList,1,"test","test","test","test","test","test", Color.DEFAULT);
	}
}
