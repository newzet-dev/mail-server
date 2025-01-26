package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.newzet.api.newsletter.domain.NewsletterStatus;

class NewsletterEntityRepositoryTest {

	private NewsletterJpaRepository newsletterJpaRepository;
	private NewsletterEntityRepository newsletterEntityRepository;

	@BeforeEach
	void setUp() {
		newsletterJpaRepository = Mockito.mock(NewsletterJpaRepository.class);
		newsletterEntityRepository = new NewsletterEntityRepository(newsletterJpaRepository);
	}

	@Test
	void 구독한_뉴스레터가_있을때_뉴스레터_목록_반환() {
		// Given
		Long userId = 1L;
		List<NewsletterJpaEntity> expectedNewsletters = List.of(
			new NewsletterJpaEntity(1L, NewsletterStatus.REGISTERED),
			new NewsletterJpaEntity(1L, NewsletterStatus.UNREGISTERED),
			new NewsletterJpaEntity(2L, NewsletterStatus.REGISTERED),
			new NewsletterJpaEntity(2L, NewsletterStatus.UNREGISTERED));
		when(newsletterJpaRepository.findAllByUserId(userId)).thenReturn(expectedNewsletters);

		// When
		List<NewsletterJpaEntity> findNewsletterList = newsletterEntityRepository
			.findNewsletterListByUserId(userId);

		// Then
		assertNotNull(findNewsletterList);
		assertEquals(expectedNewsletters.size(), findNewsletterList.size());
		verify(newsletterJpaRepository, times(1)).findAllByUserId(userId);
	}

	@Test
	void findNewsletterListByUserId_ShouldReturnEmptyListWhenNoData() {
		// Given
		Long userId = 3L;
		when(newsletterJpaRepository.findAllByUserId(userId)).thenReturn(List.of());

		// When
		List<NewsletterJpaEntity> actualNewsletters = newsletterEntityRepository
			.findNewsletterListByUserId(userId);

		// Then
		assertNotNull(actualNewsletters);
		assertTrue(actualNewsletters.isEmpty());
		verify(newsletterJpaRepository, times(1)).findAllByUserId(userId);
	}
}
