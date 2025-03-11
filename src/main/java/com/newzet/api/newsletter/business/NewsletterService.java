package com.newzet.api.newsletter.business;

import java.util.Optional;
import java.util.concurrent.locks.Lock;

import org.springframework.stereotype.Service;

import com.newzet.api.common.cache.CacheUtil;
import com.newzet.api.common.cache.LockFactory;
import com.newzet.api.common.cache.exception.LockAcquisitionException;
import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
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
			CACHE_LOCK_WAIT_TIME, CACHE_LOCK_LEASE_TIME).orElseThrow(() ->
			new LockAcquisitionException(this.getClass().getSimpleName() + "#" +
				Thread.currentThread().getStackTrace()[2].getMethodName()));
		try {
			Optional<Newsletter> cachedNewsletter = findByDomainOnCache(domain);
			if (cachedNewsletter.isPresent()) {
				return cachedNewsletter.get();
			}

			NewsletterEntityDto newsletterEntityDto = newsletterRepository
				.findByDomainOrMailingList(domain, mailingList)
				.orElseGet(() -> newsletterRepository
					.save(name, domain, mailingList, NewsletterStatus.UNREGISTERED.name()));

			Newsletter newsletter = Newsletter.create(newsletterEntityDto.getId(),
				newsletterEntityDto.getName(), newsletterEntityDto.getDomain(),
				newsletterEntityDto.getMailingList(), newsletterEntityDto.getStatus());

			cacheUtil.set(CACHE_DOMAIN_PREFIX + domain, newsletter.toCacheDto(), CACHE_DURATION);
			return newsletter;
		} finally {
			lockFactory.unlock(lock);
		}
	}
}
