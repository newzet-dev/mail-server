package com.newzet.api.newsletter.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.common.cache.CacheUtil;
import com.newzet.api.common.cache.LockFactory;
import com.newzet.api.common.cache.exception.LockAcquisitionException;
import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.domain.Newsletter;

@ExtendWith(MockitoExtension.class)
class NewsletterServiceTest {

	@Mock
	private NewsletterRepository newsletterRepository;

	@Mock
	private CacheUtil cacheUtil;

	@Mock
	private LockFactory lockFactory;

	@Mock
	private Lock lock;

	@InjectMocks
	private NewsletterService newsletterService;

	private final String name = "test";
	private final String domain = "test@example.com";
	private final String mailingList = "test123";
	private final String status = "UNREGISTERED";
	private static final String CACHE_DOMAIN_PREFIX = "newsletter:domain";
	private final NewsletterEntityDto entityDto = NewsletterEntityDto.create(1L, name, domain,
		mailingList, status);

	@Test
	void findOrCreateNewsletter_whenNewsletterExistsInCache_ReturnNewsletterInCache() {
		// Given
		NewsletterCacheDto cacheDto = NewsletterCacheDto.create(1L, name, domain, mailingList,
			status);
		when(cacheUtil.get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class)).thenReturn(
			Optional.of(cacheDto));

		// When
		Newsletter newsletter = newsletterService.findOrCreateNewsletter(name, domain,
			mailingList);

		// Then
		verifyValue(newsletter);
		verify(lockFactory, never()).tryLock(any(), anyLong(), anyLong());
		verify(newsletterRepository, never()).findByDomainOrMailingList(any(), any());
	}

	@Test
	public void findOrCreateNewsletter_whenNewsletterNoExistInCache_returnNewsletterInDB() {
		// Given
		when(cacheUtil.get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class)).thenReturn(
			Optional.empty());
		when(lockFactory.tryLock(any(), anyLong(), anyLong())).thenReturn(Optional.of(lock));
		when(newsletterRepository.findByDomainOrMailingList(domain, mailingList)).thenReturn(
			Optional.of(entityDto));

		// When
		Newsletter newsletter = newsletterService.findOrCreateNewsletter(name, domain, mailingList);

		// Then
		verifyValue(newsletter);
		verify(lockFactory).tryLock(eq(CACHE_DOMAIN_PREFIX + ":" + domain), anyLong(), anyLong());
		verify(cacheUtil, times(2)).get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class);
		verify(newsletterRepository).findByDomainOrMailingList(domain, mailingList);
		verify(newsletterRepository, never()).save(any(), any(), any(), any());
		verify(cacheUtil).set(eq(CACHE_DOMAIN_PREFIX + domain), any(), anyLong());
		verify(lockFactory).unlock(lock);
	}

	@Test
	void findOrCreateNewsletter_whenNewsletterNoExist_createAndReturnNewsletter() {
		// Given
		when(cacheUtil.get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class)).thenReturn(
			Optional.empty());
		when(lockFactory.tryLock(any(), anyLong(), anyLong())).thenReturn(Optional.of(lock));
		when(newsletterRepository.findByDomainOrMailingList(domain, mailingList)).thenReturn(
			Optional.empty());
		when(newsletterRepository.save(any(), any(), any(), any())).thenReturn(entityDto);

		// When
		Newsletter newsletter = newsletterService.findOrCreateNewsletter(name, domain, mailingList);

		// Then
		verifyValue(newsletter);
		verify(lockFactory).tryLock(eq(CACHE_DOMAIN_PREFIX + ":" + domain), anyLong(), anyLong());
		verify(cacheUtil, times(2)).get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class);
		verify(newsletterRepository).findByDomainOrMailingList(domain, mailingList);
		verify(newsletterRepository).save(name, domain, mailingList, status);
		verify(cacheUtil).set(eq(CACHE_DOMAIN_PREFIX + domain), any(), anyLong());
		verify(lockFactory).unlock(lock);
	}

	@Test
	public void findOrCreateNewsletter_whenLockAcquisitionFails_throwException() {
		//Given
		when(cacheUtil.get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class)).thenReturn(
			Optional.empty());
		when(lockFactory.tryLock(any(), anyLong(), anyLong())).thenReturn(Optional.empty());

		//When, Then
		assertThrows(LockAcquisitionException.class,
			() -> newsletterService.findOrCreateNewsletter(name, domain, mailingList));
	}

	private void verifyValue(Newsletter newsletter) {
		assertEquals(name, newsletter.getName());
		assertEquals(domain, newsletter.getDomain());
		assertEquals(mailingList, newsletter.getMailingList());
	}
}
