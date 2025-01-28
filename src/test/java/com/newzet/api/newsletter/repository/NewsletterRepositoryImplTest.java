package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;

@SpringBootTest
class NewsletterRepositoryImplTest {

	@Autowired
	private NewsletterRepositoryImpl newsletterRepository;

	@Test
	public void 뉴스레터_저장() {
		// Given
		String name = "test";
		String domain = "test@example.com";
		String maillingList = "test123";
		NewsletterStatus status = NewsletterStatus.REGISTERED;

		// When
		Newsletter savedNewsletter = newsletterRepository.save(name, domain, maillingList, status);

		// Then
		assertEquals(1L, savedNewsletter.getId());
		assertEquals(name, savedNewsletter.getName());
		assertEquals(domain, savedNewsletter.getDomain());
		assertEquals(maillingList, savedNewsletter.getMaillingList());
		assertEquals(status, savedNewsletter.getStatus());
	}
}
