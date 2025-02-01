package com.newzet.api.newsletter.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.newsletter.business.NewsletterRepository;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsletterRepositoryImpl implements NewsletterRepository {

	private final NewsletterJpaRepository newsletterJpaRepository;
	private final NewsletterCacheRepository newsletterCacheRepository;

	@Override
	@Transactional
	public Newsletter save(String name, String domain, String mailingList,
		NewsletterStatus status) {
		NewsletterEntity newsletterEntity = NewsletterEntity.create(name, domain, mailingList,
			NewsletterEntityStatus.valueOf(status.name()));
		return newsletterJpaRepository.save(newsletterEntity).toModel();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Newsletter> findByDomainOrMailingList(String domain,
		String mailingList) {
		Optional<NewsletterEntity> cachedNewsletter = findByDomainOrMailingListOnCache(
			domain, mailingList);
		if (cachedNewsletter.isPresent()) {
			return cachedNewsletter.map(NewsletterEntity::toModel);
		}

		return findByDomainOrMailingListOnDatabase(domain, mailingList)
			.map(NewsletterEntity::toModel);
	}

	private Optional<NewsletterEntity> findByDomainOrMailingListOnCache(String domain,
		String mailingList) {
		if (mailingList != null) {
			Optional<NewsletterEntity> cachedNewsletterByMailingList = newsletterCacheRepository
				.findByMailingList(mailingList);
			if (cachedNewsletterByMailingList.isPresent()) {
				return cachedNewsletterByMailingList;
			}
		}
		return newsletterCacheRepository.findByDomain(domain);
	}

	private Optional<NewsletterEntity> findByDomainOrMailingListOnDatabase(String domain,
		String mailingList) {
		return newsletterJpaRepository.findNewsletterByDomainOrMailingList(domain, mailingList)
			.map(newsletterEntity -> saveOnCache(domain, mailingList, newsletterEntity));
	}

	private NewsletterEntity saveOnCache(String domain, String mailingList,
		NewsletterEntity newsletterEntity) {
		newsletterCacheRepository.saveByDomain(domain, newsletterEntity);
		if (mailingList != null) {
			newsletterCacheRepository.saveByMailingList(mailingList, newsletterEntity);
		}
		return newsletterEntity;
	}
}
