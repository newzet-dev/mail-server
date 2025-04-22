package com.newzet.api.newsletter.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;
import com.newzet.api.newsletter.fixture.NewsletterFixture;
import com.newzet.api.newsletter.repository.NewsletterEntity;

class NewsletterTest {

	@Test
	public void 뉴스레터_생성() {
		//When
		NewsletterEntity newsletter = NewsletterFixture.createDefaultEntity();

		//Then
		// assertEquals(1L, newsletter.getId());
		assertEquals("test", newsletter.getName());
		assertEquals("test@example.com", newsletter.getDomain());
		assertEquals("test123", newsletter.getMailingList());
	}

	@Test
	public void toCacheDto_returnNewsletterCacheDto() {
		//Given
		Long id = 1L;
		String name = "test";
		String domain = "test@example.com";
		String mailingList = "test123";
		String status = "UNREGISTERED";
		NewsletterEntity newsletter = NewsletterFixture.createDefaultEntity();

		//When
		NewsletterCacheDto dto = newsletter.toCacheDto();

		//Then
		assertEquals(newsletter.getId(), dto.getId());
		assertEquals(newsletter.getName(), dto.getName());
		assertEquals(newsletter.getDomain(), dto.getDomain());
		assertEquals(newsletter.getMailingList(), dto.getMailingList());
	}
}
