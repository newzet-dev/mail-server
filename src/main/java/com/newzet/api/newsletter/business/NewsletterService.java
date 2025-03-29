package com.newzet.api.newsletter.business;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.common.cache.CacheUtil;
import com.newzet.api.common.lock.LockFactory;
import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewsletterService {

	private static final Long CACHE_LOCK_WAIT_TIME = 1000 * 5L;
	private static final Long CACHE_LOCK_LEASE_TIME = 1000 * 3L;
	private static final Long CACHE_DURATION = 1000 * 60 * 5L;
	private static final String CACHE_DOMAIN_PREFIX = "newsletter:domain";

	private final NewsletterRepository newsletterRepository;
	private final CacheUtil cacheUtil;
	private final LockFactory lockFactory;

	public Newsletter findOrCreateNewsletter(String name, String domain, String mailingList) {
		return findByDomainOnCache(domain)
			.orElseGet(() -> findOrCreateByDomainOrMailingListWithLock(name, domain, mailingList));
	}

	private Optional<Newsletter> findByDomainOnCache(String domain) {
		return cacheUtil.get(CACHE_DOMAIN_PREFIX + domain, NewsletterCacheDto.class)
			.map(dto -> Newsletter.create(dto.getId(), dto.getName(), dto.getDomain(),
				dto.getMailingList(), dto.getStatus()));
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
				cacheUtil.set(CACHE_DOMAIN_PREFIX + domain, newsletter.toCacheDto(), CACHE_DURATION);
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
