package com.newzet.api.newsletter.business.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.newzet.api.common.cache.CacheUtil;
import com.newzet.api.newsletter.business.dto.NewsletterImageUrlCacheDto;
import com.newzet.api.newsletter.business.repository.NewsletterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsletterImageUrlService {
	private static final String CACHE_DOMAIN_PREFIX = "nImgUrl:domain:";
	private static final String CACHE_MAILING_LIST_PREFIX = "nImgUrl:mList:";
	private static final Long CACHE_DURATION = 1000 * 60 * 24 * 7L; // 7일
	private final NewsletterRepository newsletterRepository;
	private final CacheUtil cacheUtil;

	private NewsletterImageUrlCacheDto findByDomainOrMailingList(String domain,
		String mailingList) {
		return findByDomainOrMailingListOnCache(domain, mailingList).orElseGet(
			() -> findByDomainOrMailingListOnDatabase(domain, mailingList));
	}

	private NewsletterImageUrlCacheDto findByDomainOrMailingListOnDatabase(String domain,
		String mailingList) {
		NewsletterImageUrlCacheDto cacheDto = newsletterRepository.findNewsLetterImageUrlByDomainAndMailingList(
			domain, mailingList);
		cacheUtil.set(CACHE_DOMAIN_PREFIX + domain, cacheDto, CACHE_DURATION);
		cacheUtil.set(CACHE_MAILING_LIST_PREFIX + domain, cacheDto, CACHE_DURATION);

		return cacheDto;
	}

	private Optional<NewsletterImageUrlCacheDto> findByDomainOrMailingListOnCache(String domain,
		String mailingList) {
		Optional<NewsletterImageUrlCacheDto> cacheDtoByDomain = cacheUtil.get(
			CACHE_DOMAIN_PREFIX + domain, NewsletterImageUrlCacheDto.class);

		if (cacheDtoByDomain.isEmpty()) {
			return cacheUtil.get(
				CACHE_MAILING_LIST_PREFIX + mailingList, NewsletterImageUrlCacheDto.class);
		}

		return cacheDtoByDomain;
	}
}
