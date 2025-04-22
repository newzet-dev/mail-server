package com.newzet.api.newsletter.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.category.repository.CategoryEntity;
import com.newzet.api.common.cache.CacheUtil;
import com.newzet.api.common.exception.InternalErrorException;
import com.newzet.api.common.lock.LockFactory;
import com.newzet.api.common.lock.exception.LocalLockAcquisitionException;
import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;
import com.newzet.api.newsletter.controller.dto.NewsletterInfoResponse;
import com.newzet.api.newsletter.controller.dto.NewsletterListResponse;
import com.newzet.api.newsletter.fixture.NewsletterFixture;
import com.newzet.api.newsletter.repository.NewsletterEntity;

@ExtendWith(MockitoExtension.class)
class NewsletterServiceTest {

	private static final String CACHE_DOMAIN_PREFIX = "newsletter:domain";
	private final String name = "test";
	private final String domain = "test@example.com";
	private final String mailingList = "test123";
	private final String status = "UNREGISTERED";
	private final CategoryEntity categoryEntity = CategoryEntity.create(UUID.randomUUID(), "test", "test", "test");
	private final NewsletterEntity entity = NewsletterFixture.createDefaultEntity(categoryEntity);
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

	@Test
	void findOrCreateNewsletter_whenNewsletterExistsInCache_ReturnNewsletterInCache() {
		// Given
		NewsletterCacheDto cacheDto = NewsletterFixture.createDefaultCacheDto(categoryEntity);
		when(cacheUtil.get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class)).thenReturn(
			Optional.of(cacheDto));

		// When
		NewsletterEntity newsletter = newsletterService.findOrCreateNewsletter(name, domain,
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
		when(lockFactory.tryLock(any(), anyLong(), anyLong())).thenReturn(lock);
		when(newsletterRepository.findByDomainOrMailingList(domain, mailingList)).thenReturn(
			Optional.of(entity));

		// When
		NewsletterEntity newsletter = newsletterService.findOrCreateNewsletter(name, domain, mailingList);

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
		when(lockFactory.tryLock(any(), anyLong(), anyLong())).thenReturn(lock);
		when(newsletterRepository.findByDomainOrMailingList(domain, mailingList)).thenReturn(
			Optional.empty());
		when(newsletterRepository.save(any(), any(), any(), any())).thenReturn(entity);

		// When
		NewsletterEntity newsletter = newsletterService.findOrCreateNewsletter(name, domain, mailingList);

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
		when(lockFactory.tryLock(any(), anyLong(), anyLong())).thenThrow(new LocalLockAcquisitionException("Local Lock 획득에 실패하였습니다."));

		// When
		assertThrows(InternalErrorException.class,
			() -> newsletterService.findOrCreateNewsletter(name, domain, mailingList));

		// Then
		verify(cacheUtil, times(1)).get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class);
		verify(newsletterRepository, never()).findByDomainOrMailingList(domain, mailingList);
		verify(cacheUtil, never()).set(eq(CACHE_DOMAIN_PREFIX + domain), any(), anyLong());
		verify(lockFactory, never()).unlock(lock);
	}

	@Test
	public void searchNewsletterListByNameOrCategoryId() {
		// Given
		NewsletterEntity newsletter = NewsletterFixture.createEntityWithId();
		List<NewsletterEntity> newsletterList = new ArrayList<>();
		newsletterList.add(newsletter);
		when(newsletterRepository.findNewsLetterListByNameOrCategoryId(any(String.class), any(UUID.class)))
			.thenReturn(newsletterList);

		// When
		NewsletterListResponse searchList = newsletterService.searchNewsletterListByNameOrCategoryId(
			newsletter.getName(), categoryEntity.getId());

		// Then
		verify(newsletterRepository, times(1)).findNewsLetterListByNameOrCategoryId(any(String.class), any(UUID.class));
		assertEquals(1, searchList.newsletterList().size());
	}

	@Test
	public void getNewsletterById() {
		// Given
		NewsletterEntity newsletter = NewsletterFixture.createEntityWithId();
		when(newsletterRepository.getById(any(UUID.class)))
			.thenReturn(newsletter);

		// When
		NewsletterInfoResponse newsletterInfoResponse = newsletterService.getNewsLetterById(
			newsletter.getId());

		// Then
		verify(newsletterRepository, times(1)).getById(any(UUID.class));
		assertEquals(newsletterInfoResponse.id(), newsletter.getId().toString());
	}

	private void verifyValue(NewsletterEntity newsletter) {
		assertEquals(name, newsletter.getName());
		assertEquals(domain, newsletter.getDomain());
		assertEquals(mailingList, newsletter.getMailingList());
	}
}
