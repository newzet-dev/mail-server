package com.newzet.api.newsletter.repository;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;

class NewsletterRepositoryImplTest {

	private NewsletterJpaRepository newsletterJpaRepository;
	private NewsletterRepositoryImpl newsletterRepository;

	@BeforeEach
	void setUp() {
		newsletterJpaRepository = Mockito.mock(newsletterJpaRepository.getClass());
		newsletterRepository = new NewsletterRepositoryImpl(newsletterJpaRepository);
	}

	@Test
	public void 뉴스레터가_존재하는_경우_뉴스레터를_리턴() {
		//Given
		String existDomain = "exist@example.com";
		String existMaillingList = "exist1234";
		when(newsletterJpaRepository
			.findNewsletterByDomainOrMaillingList(existDomain, existMaillingList))
			.thenReturn(Optional.of(new NewsletterJpaEntity(1L, NewsletterStatus.REGISTERED)));

		//When
		Newsletter newsletter = newsletterRepository.findNewsletter(existDomain, existMaillingList);

		//Then
		Assertions.assertInstanceOf(Newsletter.class, newsletter);
	}
}
