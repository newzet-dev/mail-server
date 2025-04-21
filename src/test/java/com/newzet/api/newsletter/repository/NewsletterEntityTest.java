package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.fixture.NewsletterFixture;

class NewsletterEntityTest {

	@Test
	public void create_returnNewsletterEntity() {
		//When
		NewsletterEntity newsletterEntity = NewsletterFixture.createDefaultEntity();

		//Then
		assertEquals("test", newsletterEntity.getName());
		assertEquals("test@example.com", newsletterEntity.getDomain());
		assertEquals("test123", newsletterEntity.getMailingList());
	}

	@Test
	public void toEntityDto_returnNewsletterEntityDto() {
		//Given
		NewsletterEntity newsletterEntity = NewsletterFixture.createDefaultEntity();
		//When
		NewsletterEntityDto newsletter = newsletterEntity.toEntityDto();

		//Then
		assertEquals(newsletterEntity.getId(), newsletter.getId());
		assertEquals(newsletterEntity.getDomain(), newsletter.getDomain());
		assertEquals(newsletterEntity.getMailingList(), newsletter.getMailingList());
	}
}
