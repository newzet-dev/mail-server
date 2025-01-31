package com.newzet.api.newsletter.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NewsletterTest {

	@Test
	public void 뉴스레터_생성() {
		//When
		Newsletter newsletter = Newsletter.create(1L, "test",
			"test@example.com", "test123", NewsletterStatus.UNREGISTERED);

		//Then
		assertEquals(1L, newsletter.getId());
		assertEquals("test", newsletter.getName());
		assertEquals("test@example.com", newsletter.getDomain());
		assertEquals("test123", newsletter.getMaillingList());
		assertEquals(NewsletterStatus.UNREGISTERED, newsletter.getStatus());
	}
}
