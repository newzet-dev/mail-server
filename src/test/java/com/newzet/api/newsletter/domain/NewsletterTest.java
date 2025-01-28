package com.newzet.api.newsletter.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NewsletterTest {

	@Test
	public void 뉴스레터_생성() {
		//When
		Newsletter newsletter = Newsletter.create(1L, "test",
			"test@example.com", "test123", NewsletterStatus.UNREGISTERED);

		//Then
		Assertions.assertEquals(1L, newsletter.getId());
		Assertions.assertEquals("test", newsletter.getName());
		Assertions.assertEquals("test@example.com", newsletter.getMaillingList());
		Assertions.assertEquals(NewsletterStatus.UNREGISTERED, newsletter.getStatus());
	}
}
