package com.newzet.api.newsletter.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.advertise.business.AdvertiseRepository;
import com.newzet.api.common.cache.CacheUtil;
import com.newzet.api.common.lock.LockFactory;
import com.newzet.api.common.util.UuidConverter;
import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.controller.dto.NewsletterInfoResponse;
import com.newzet.api.newsletter.controller.dto.NewsletterListResponse;
import com.newzet.api.newsletter.controller.dto.NewsletterRecommendResponse;
import com.newzet.api.newsletter.controller.dto.NewsletterResponse;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;
import com.newzet.api.usercategory.business.UserCategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NewsletterService {

	private static final Long CACHE_LOCK_WAIT_TIME = 1000 * 5L;
	private static final Long CACHE_LOCK_LEASE_TIME = 1000 * 3L;
	private static final Long CACHE_DURATION = 1000 * 60 * 5L;
	private static final String CACHE_DOMAIN_PREFIX = "newsletter:domain";

	private final NewsletterRepository newsletterRepository;
	private final UserCategoryRepository userCategoryRepository;
	private final AdvertiseRepository advertiseRepository;
	private final CacheUtil cacheUtil;
	private final LockFactory lockFactory;

	@Transactional
	public Newsletter findOrCreateNewsletter(String name, String domain, String mailingList) {
		return findByDomainOnCache(domain)
			.orElseGet(() -> findOrCreateByDomainOrMailingListWithLock(name, domain, mailingList));
	}

	public NewsletterListResponse searchNewsletterListByNameOrCategoryId(String name,
		String categoryId) {
		UUID categoryUuid = UuidConverter.convert(categoryId);
		List<NewsletterResponse> newsletterResponseList = newsletterRepository.findNewsLetterListByNameOrCategoryId(
				name, categoryUuid).stream()
			.map(newsletterEntity -> NewsletterResponse.create(newsletterEntity.getId(),
				newsletterEntity.getName(), newsletterEntity.getImageUrl(),
				newsletterEntity.getDescription(), newsletterEntity.getPriority()))
			.toList();

		return NewsletterListResponse.create(newsletterResponseList);
	}

	public NewsletterInfoResponse getNewsLetterById(String newsletterId) {
		UUID newsletterUuid = UuidConverter.convert(newsletterId);
		NewsletterEntityDto newsletter = newsletterRepository.getById(newsletterUuid);
		return NewsletterInfoResponse.create(newsletter.getId(), newsletter.getName(),
			newsletter.getImageUrl(), newsletter.getDetail(),
			newsletter.getStatus(),
			newsletter.getDayOfWeek(), newsletter.getSubscriptionUrl(), false,
			newsletter.getCategory().getName());
	}

	public NewsletterRecommendResponse recommendNewsletterList(UUID userId) {
		List<UUID> userCategoryIdList = userCategoryRepository.getUserCategoryListByUserId(
				userId).stream()
			.map(userCategoryEntityDto -> userCategoryEntityDto.getCategory().getId())
			.toList();

		List<Newsletter> newsletterListInUserCategories = newsletterRepository.getNewsLetterListByCategoryIdList(
				userCategoryIdList).stream()
			.map(NewsletterEntityDto::toDomain)
			.toList();

		List<Newsletter> advertiseNewsletterList = advertiseRepository.getAdvertiseNewsletterIdList()
			.stream()
			.map(advertiseEntityDto -> newsletterRepository.getById(
				advertiseEntityDto.getNewsletter().getId()))
			.map(NewsletterEntityDto::toDomain)
			.toList();

		int recommendNumber = 4 - advertiseNewsletterList.size();
		List<Newsletter> randomNewsletterList = getRandomNewsletterList(newsletterListInUserCategories,
			recommendNumber);
		List<Newsletter> combinedRecommendNewsletterList = Stream.of(advertiseNewsletterList,
				randomNewsletterList)
			.flatMap(List::stream)
			.toList();

		return NewsletterRecommendResponse.create(combinedRecommendNewsletterList.stream()
			.map(newsletter -> NewsletterResponse.create(newsletter.getId(),
				newsletter.getName(), newsletter.getImageUrl(), newsletter.getDescription(),
				newsletter.getPriority()))
			.toList());
	}

	protected List<Newsletter> getRandomNewsletterList(List<Newsletter> newsletterList, int count) {
		List<Newsletter> randomNewsletterList = new ArrayList<>(newsletterList);
		Collections.shuffle(randomNewsletterList);
		return randomNewsletterList.subList(0, count);
	}

	private Optional<Newsletter> findByDomainOnCache(String domain) {
		return cacheUtil.get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class)
			.map(NewsletterCacheDto::toDomain);
	}

	private Newsletter findOrCreateByDomainOrMailingListWithLock(String name, String domain,
		String mailingList) {
		Lock lock = lockFactory.tryLock(CACHE_DOMAIN_PREFIX + ":" + domain,
			CACHE_LOCK_WAIT_TIME, CACHE_LOCK_LEASE_TIME);
		try {
			return findByDomainOnCache(domain).orElseGet(
				() -> findOrCreateByDomainOrMailingListInDatabase(name, domain, mailingList));
		} finally {
			lockFactory.unlock(lock);
		}
	}

	private Newsletter findOrCreateByDomainOrMailingListInDatabase(String name, String domain,
		String mailingList) {
		return newsletterRepository
			.findByDomainOrMailingList(domain, mailingList)
			.map(NewsletterEntityDto::toDomain)
			.map(newsletter -> {
				cacheUtil.set(CACHE_DOMAIN_PREFIX + domain, newsletter.toCacheDto(),
					CACHE_DURATION);
				return newsletter;
			})
			.orElseGet(() -> createNewsletter(name, domain, mailingList));
	}

	private Newsletter createNewsletter(String name, String domain, String mailingList) {
		Newsletter savedNewsletter = newsletterRepository.save(name, domain, mailingList,
			NewsletterStatus.UNREGISTERED.name()).toDomain();
		cacheUtil.set(CACHE_DOMAIN_PREFIX + domain, savedNewsletter.toCacheDto(), CACHE_DURATION);
		return savedNewsletter;
	}
}
