package com.newzet.api.newsletter.business;

import java.util.Optional;

import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;

public interface NewsletterRepository {

	NewsletterEntityDto save(String name, String domain, String mailingList, String status);

	Optional<NewsletterEntityDto> findByDomainOrMailingList(String domain, String mailingList);
}
