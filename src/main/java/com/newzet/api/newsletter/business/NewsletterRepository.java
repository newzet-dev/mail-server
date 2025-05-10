package com.newzet.api.newsletter.business;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;

public interface NewsletterRepository {

	NewsletterEntityDto save(String name, String domain, String mailingList, String status);

	Optional<NewsletterEntityDto> findByDomainOrMailingList(String domain, String mailingList);

	List<NewsletterEntityDto> findNewsLetterListByName(String name);

	List<NewsletterEntityDto> findNewsLetterListByCategoryId(UUID categoryId);

	NewsletterEntityDto getById(UUID id);

	List<NewsletterEntityDto> getNewsLetterListByCategoryIdList(List<UUID> categoryIdList);
}
