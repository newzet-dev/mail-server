package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.RegisteredNewsletter;

class NewsletterRepositoryImplTest {

	private NewsletterJpaRepository newsletterJpaRepository;
	private NewsletterRepositoryImpl newsletterRepository;

	@BeforeEach
	void setUp() {
		newsletterJpaRepository = Mockito.mock(NewsletterJpaRepository.class);
		newsletterRepository = new NewsletterRepositoryImpl(newsletterJpaRepository);
	}

	@Test
	public void UnregisteredNewsletter를_생성_및_반환() {
		//Given
		String name = "test";
		String domain = "test@example.com";
		String maillingList = "test1234";

		newsletterRepository.createAndGetUnregisteredNewsletter(name, domain, maillingList);

		//When

		//Then
	}

	@Test
	public void 뉴스레터가_존재하는_경우_뉴스레터를_리턴() {
		//Given
		String existDomain = "exist@example.com";
		String existMaillingList = "exist1234";
		NewsletterEntity newsletterEntity = Mockito.mock(NewsletterEntity.class);
		when(newsletterEntity.toNewsletter()).thenReturn(new RegisteredNewsletter());
		when(newsletterJpaRepository
			.findNewsletterByDomainOrMaillingList(existDomain, existMaillingList))
			.thenReturn(Optional.of(newsletterEntity));

		//When
		Newsletter newsletter = newsletterRepository.findNewsletter(existDomain, existMaillingList);

		//Then
		assertInstanceOf(Newsletter.class, newsletter);
		verify(newsletterEntity).toNewsletter();
	}

	@Test
	public void 뉴스레터가_존재하지_않는_경우_null을_리턴() {
		//Given
		String noExistDomain = "no_exist@example.com";
		String noExistMaillingList = "no_exist1234";
		when(newsletterJpaRepository
			.findNewsletterByDomainOrMaillingList(noExistDomain, noExistMaillingList))
			.thenReturn(Optional.empty());

		//When
		Newsletter newsletter = newsletterRepository
			.findNewsletter(noExistDomain, noExistMaillingList);

		//Then
		assertNull(newsletter);
	}
}
