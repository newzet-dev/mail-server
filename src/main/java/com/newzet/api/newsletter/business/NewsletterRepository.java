package com.newzet.api.newsletter.business;

import java.util.Optional;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;

public interface NewsletterRepository {

	Newsletter save(String name, String domain, String mailingList, NewsletterStatus status);

	Optional<Newsletter> findByDomainOrMailingList(String domain, String mailingList);
}
