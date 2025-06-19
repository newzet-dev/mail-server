package com.newzet.api.newsletter.business.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.common.cache.CacheUtil;
import com.newzet.api.newsletter.business.dto.NewsletterImageUrlCacheDto;
import com.newzet.api.newsletter.business.repository.NewsletterRepository;

@ExtendWith(MockitoExtension.class)
class NewsletterImageUrlServiceTest {

	@InjectMocks
	private NewsletterImageUrlService newsletterImageUrlService;

	@Mock
	private NewsletterRepository newsletterRepository;

	@Mock
	private CacheUtil cacheUtil;

	// 테스트에서 공통으로 사용할 변수들
	private String domain = "test.com";
	private String mailingList = "newsletter";
	private String domainKey = "nImgUrl:domain:" + domain;
	private String mailingListKey = "nImgUrl:mList:" + mailingList;
	private NewsletterImageUrlCacheDto cacheDto = new NewsletterImageUrlCacheDto(
		"http://image.url");

	@Test
	@DisplayName("모든 캐시가 비어있을 때 DB에서 조회하고 캐시에 저장한다")
	void findByDomainOrMailingList_whenAllCacheMiss_thenFindFromDBAndSetCache() {
		// given
		when(cacheUtil.get(eq(domainKey), any()))
			.thenReturn(Optional.empty());
		when(cacheUtil.get(eq(mailingListKey), any()))
			.thenReturn(Optional.empty());

		when(newsletterRepository.findNewsLetterImageUrlByDomainAndMailingList(domain, mailingList))
			.thenReturn(cacheDto);

		// when
		NewsletterImageUrlCacheDto resultDto = newsletterImageUrlService.findByDomainOrMailingList(
			domain, mailingList);

		// then
		assertThat(resultDto).isEqualTo(cacheDto);
		verify(newsletterRepository, times(1)).findNewsLetterImageUrlByDomainAndMailingList(domain,
			mailingList);
		verify(cacheUtil, times(1)).set(eq(domainKey), eq(cacheDto), anyLong());
		verify(cacheUtil, times(1)).set(eq(mailingListKey), eq(cacheDto), anyLong());
	}

	@Test
	@DisplayName("Domain 키로 캐시 조회 성공 시 DB를 조회하지 않는다")
	void findByDomainOrMailingList_whenPrimaryCacheHit_thenDoNotQueryDB() {
		// given
		when(cacheUtil.get(eq(domainKey), any())).thenReturn(
			Optional.of(cacheDto));

		// when
		NewsletterImageUrlCacheDto resultDto = newsletterImageUrlService.findByDomainOrMailingList(
			domain, mailingList);

		// then
		assertThat(resultDto).isEqualTo(cacheDto);
		verify(cacheUtil, never()).get(eq(mailingListKey), any());
		verify(newsletterRepository, never()).findNewsLetterImageUrlByDomainAndMailingList(any(),
			any());
		verify(cacheUtil, never()).set(anyString(), any(), anyLong());
	}

	@Test
	@DisplayName("Mailing List 키로 캐시 조회 성공 시 DB를 조회하지 않는다")
	void findByDomainOrMailingList_whenSecondaryCacheHit_thenDoNotQueryDB() {
		// given
		when(cacheUtil.get(eq(domainKey), any())).thenReturn(
			Optional.empty());
		when(cacheUtil.get(eq(mailingListKey), any())).thenReturn(
			Optional.of(cacheDto));

		// when
		NewsletterImageUrlCacheDto resultDto = newsletterImageUrlService.findByDomainOrMailingList(
			domain, mailingList);

		// then
		assertThat(resultDto).isEqualTo(cacheDto);

		verify(newsletterRepository, never()).findNewsLetterImageUrlByDomainAndMailingList(any(),
			any());
		verify(cacheUtil, never()).set(anyString(), any(), anyLong());
		verify(cacheUtil, times(1)).get(eq(domainKey), any());
		verify(cacheUtil, times(1)).get(eq(mailingListKey), any());
	}
}