package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.newzet.api.newsletter.fixture.NewsletterFixture;
import com.newzet.api.newsletter.repository.entity.NewsletterEntity;

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

}
