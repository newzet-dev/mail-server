package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.newzet.api.newsletter.domain.Newsletter;

public class NewsletterRepositoryImplCacheTest {
	@Mock
	private NewsletterCacheRepository newsletterCacheRepository;

	@Mock
	private NewsletterJpaRepository newsletterJpaRepository;

	@InjectMocks
	private NewsletterRepositoryImpl newsletterRepositoryImpl;

	private final String DOMAIN = "test.com";
	private final String MAILING_LIST = "test@mail.com";
	private final NewsletterEntity newsletterEntity = NewsletterEntity
		.create("Test", DOMAIN, MAILING_LIST, NewsletterEntityStatus.REGISTERED);

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void 캐시에_mailingList가_동일한_값이_존재하는_경우_DB를_조회하지_않음() {
		// Given
		when(newsletterCacheRepository.findByMailingList(MAILING_LIST)).thenReturn(
			Optional.of(newsletterEntity));

		// When
		Optional<Newsletter> newsletter = newsletterRepositoryImpl.findByDomainOrMailingList(DOMAIN,
			MAILING_LIST);

		// Then
		assertTrue(newsletter.isPresent());
		assertEquals(DOMAIN, newsletter.get().getDomain());
		verify(newsletterCacheRepository, times(1)).findByMailingList(MAILING_LIST);
		verify(newsletterCacheRepository, times(0)).findByDomain(DOMAIN);
		verify(newsletterJpaRepository, never()).findNewsletterByDomainOrMailingList(any(), any());
	}

	@Test
	void 캐시에_domain이_동일한_값이_존재하는_경우_DB를_조회하지_않음() {
		// Given
		when(newsletterCacheRepository.findByDomain(DOMAIN)).thenReturn(
			Optional.of(newsletterEntity));

		// When
		Optional<Newsletter> newsletter = newsletterRepositoryImpl.findByDomainOrMailingList(DOMAIN,
			null);

		// Then
		assertTrue(newsletter.isPresent());
		assertEquals(DOMAIN, newsletter.get().getDomain());
		verify(newsletterCacheRepository, never()).findByMailingList(any());
		verify(newsletterCacheRepository, times(1)).findByDomain(DOMAIN);
		verify(newsletterJpaRepository, never()).findNewsletterByDomainOrMailingList(any(), any());
	}

	@Test
	void 캐시에_없고_DB에서_mailingList로_찾으면_캐시에_mailingList와_domain으로_저장됨() {
		// Given
		when(newsletterCacheRepository.findByMailingList(MAILING_LIST)).thenReturn(
			Optional.empty());
		when(newsletterCacheRepository.findByDomain(DOMAIN)).thenReturn(Optional.empty());
		when(newsletterJpaRepository.findNewsletterByDomainOrMailingList(DOMAIN, MAILING_LIST))
			.thenReturn(Optional.of(newsletterEntity));

		// When
		Optional<Newsletter> newsletter = newsletterRepositoryImpl.findByDomainOrMailingList(DOMAIN,
			MAILING_LIST);

		// Then
		assertTrue(newsletter.isPresent());
		assertEquals(DOMAIN, newsletter.get().getDomain());

		verify(newsletterCacheRepository, times(1)).findByMailingList(MAILING_LIST);
		verify(newsletterCacheRepository, times(1)).findByDomain(DOMAIN);
		verify(newsletterJpaRepository, times(1)).findNewsletterByDomainOrMailingList(DOMAIN,
			MAILING_LIST);

		verify(newsletterCacheRepository, times(1)).saveByDomain(DOMAIN, newsletterEntity);
		verify(newsletterCacheRepository, times(1)).saveByMailingList(MAILING_LIST,
			newsletterEntity);
	}

	@Test
	void 캐시에_없고_DB에서_domain으로_찾을_때_mailingList가_null이면_캐시에_domain으로_저장됨() {
		// Given
		when(newsletterCacheRepository.findByDomain(DOMAIN)).thenReturn(Optional.empty());
		when(newsletterJpaRepository.findNewsletterByDomainOrMailingList(DOMAIN, null))
			.thenReturn(Optional.of(newsletterEntity));

		// When
		Optional<Newsletter> newsletter = newsletterRepositoryImpl.findByDomainOrMailingList(DOMAIN,
			null);

		// Then
		assertTrue(newsletter.isPresent());
		assertEquals(DOMAIN, newsletter.get().getDomain());

		verify(newsletterCacheRepository, never()).findByMailingList(any());
		verify(newsletterCacheRepository, times(1)).findByDomain(DOMAIN);
		verify(newsletterJpaRepository, times(1)).findNewsletterByDomainOrMailingList(DOMAIN, null);

		verify(newsletterCacheRepository, times(1)).saveByDomain(DOMAIN, newsletterEntity);
		verify(newsletterCacheRepository, never()).saveByMailingList(any(), any());
	}

	@Test
	void 캐시와_DB_모두_없는_경우_Optional_empty() {
		// Given
		when(newsletterCacheRepository.findByMailingList(MAILING_LIST)).thenReturn(
			Optional.empty());
		when(newsletterCacheRepository.findByDomain(DOMAIN)).thenReturn(Optional.empty());
		when(newsletterJpaRepository.findNewsletterByDomainOrMailingList(DOMAIN, MAILING_LIST))
			.thenReturn(Optional.empty());

		// When
		Optional<Newsletter> result = newsletterRepositoryImpl.findByDomainOrMailingList(DOMAIN,
			MAILING_LIST);

		// Then
		assertTrue(result.isEmpty());

		verify(newsletterCacheRepository, times(1)).findByMailingList(MAILING_LIST);
		verify(newsletterCacheRepository, times(1)).findByDomain(DOMAIN);
		verify(newsletterJpaRepository, times(1)).findNewsletterByDomainOrMailingList(DOMAIN,
			MAILING_LIST);

		verify(newsletterCacheRepository, never()).saveByDomain(any(), any());
		verify(newsletterCacheRepository, never()).saveByMailingList(any(), any());
	}
}
