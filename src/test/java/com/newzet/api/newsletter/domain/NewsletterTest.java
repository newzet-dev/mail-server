package com.newzet.api.newsletter.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;

class NewsletterTest {

	@Test
	public void 뉴스레터_생성() {
		//When
		Newsletter newsletter = Newsletter.create(1L, "test",
			"test@example.com", "test123", "UNREGISTERED");

		//Then
		assertEquals(1L, newsletter.getId());
		assertEquals("test", newsletter.getName());
		assertEquals("test@example.com", newsletter.getDomain());
		assertEquals("test123", newsletter.getMailingList());
		assertEquals(NewsletterStatus.UNREGISTERED, newsletter.getStatus());
	}

	@Test
	public void toCacheDto_returnNewsletterCacheDto() {
		//Given
		Long id = 1L;
		String name = "test";
		String domain = "test@example.com";
		String mailingList = "test123";
		String status = "UNREGISTERED";
		Newsletter newsletter = Newsletter.create(id, name, domain, mailingList, status);

		//When
		NewsletterCacheDto dto = newsletter.toCacheDto();

		//Then
		assertEquals(newsletter.getId(), dto.getId());
		assertEquals(newsletter.getName(), dto.getName());
		assertEquals(newsletter.getDomain(), dto.getDomain());
		assertEquals(newsletter.getMailingList(), dto.getMailingList());
		assertEquals(newsletter.getStatus().name(), dto.getStatus());
	}
}
