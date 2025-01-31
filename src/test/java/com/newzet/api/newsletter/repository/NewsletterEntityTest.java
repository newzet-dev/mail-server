package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;

class NewsletterEntityTest {

	@Test
	public void 뉴스레터_엔티티_생성() {
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
	public void 뉴스레터_엔티티를_모델로_변환() {
		//Given
		NewsletterEntity newsletterEntity = NewsletterEntity.builder()
			.id(1L)
			.name("test")
			.domain("test@example.com")
			.mailingList("test123")
			.status(NewsletterEntityStatus.REGISTERED)
			.build();

		//When
		Newsletter newsletter = newsletterEntity.toModel();

		//Then
		assertEquals(1L, newsletter.getId());
		assertEquals("test@example.com", newsletter.getDomain());
		assertEquals("test123", newsletter.getMailingList());
		assertEquals(NewsletterStatus.REGISTERED, newsletter.getStatus());
	}
}
