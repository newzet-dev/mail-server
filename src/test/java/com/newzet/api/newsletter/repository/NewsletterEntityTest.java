package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;

class NewsletterEntityTest {

	@Test
	public void create_returnNewsletterEntity() {
		//When
		NewsletterEntity newsletterEntity = NewsletterEntity.builder()
			.name("test")
			.domain("test@example.com")
			.mailingList("test123")
			.status(NewsletterEntityStatus.REGISTERED)
			.build();

		//Then
		assertEquals("test", newsletterEntity.getName());
		assertEquals("test@example.com", newsletterEntity.getDomain());
		assertEquals("test123", newsletterEntity.getMailingList());
		assertEquals(NewsletterEntityStatus.REGISTERED, newsletterEntity.getStatus());
	}

	@Test
	public void toEntityDto_returnNewsletterEntityDto() {
		//Given
		NewsletterEntity newsletterEntity = NewsletterEntity.builder()
			.id(1L)
			.name("test")
			.domain("test@example.com")
			.mailingList("test123")
			.status(NewsletterEntityStatus.REGISTERED)
			.build();

		//When
		NewsletterEntityDto newsletter = newsletterEntity.toEntityDto();

		//Then
		assertEquals(newsletterEntity.getId(), newsletter.getId());
		assertEquals(newsletterEntity.getDomain(), newsletter.getDomain());
		assertEquals(newsletterEntity.getMailingList(), newsletter.getMailingList());
		assertEquals(newsletterEntity.getStatus().name(), newsletter.getStatus());
	}
}
