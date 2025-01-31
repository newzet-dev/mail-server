package com.newzet.api.newsletter.repository;

import java.util.Optional;

public interface NewsletterCacheRepository {
	public void saveByDomain(String domain, NewsletterEntity newsletterEntity);

	public void saveByMailingList(String mailingList, NewsletterEntity newsletterEntity);

	public Optional<NewsletterEntity> findByDomain(String domain);

	public Optional<NewsletterEntity> findByMailingList(String mailingList);
}
