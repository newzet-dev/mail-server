package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NewsletterEntityTest {

	@Test
	public void 뉴스레터_엔티티_생성() {
		//When
		NewsletterEntity newsletterEntity = NewsletterEntity.create("test", "test@example.com",
			"test123", NewsletterEntityStatus.REGISTERED);

		//Then
		assertEquals("test", newsletterEntity.getName());
		assertEquals("test@example.com", newsletterEntity.getDomain());
		assertEquals("test123", newsletterEntity.getMaillingList());
		assertEquals(NewsletterEntityStatus.REGISTERED, newsletterEntity.getStatus());
	}
}
