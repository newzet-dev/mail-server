package com.newzet.api.newsletter.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;
import com.newzet.api.newsletter.domain.RegisteredNewsletter;

class NewsletterJpaEntityTest {

	@Test
	public void 등록된_뉴스레터일때_RegisteredNewsletter_객체로_변환() {
		//Given
		NewsletterEntity newsletterEntity =
			new NewsletterEntity(1L, NewsletterStatus.REGISTERED);

		//When
		Newsletter newsletter = newsletterEntity.toNewsletter();

		//Then
		Assertions.assertEquals(newsletter.getClass(), RegisteredNewsletter.class);
	}

	@Test
	public void 등록되지_않은_뉴스레터일때_UnRegisteredNewsletter_객체로_변환() {
		//Given
		NewsletterEntity newsletterEntity =
			new NewsletterEntity(1L, NewsletterStatus.UNREGISTERED);

		//When
		Newsletter newsletter = newsletterEntity.toNewsletter();

		//Then
		Assertions.assertEquals(newsletter.getClass(), Newsletter.class);
	}
}
