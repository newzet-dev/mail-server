package com.newzet.api.newsletter.business;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.newzet.api.newsletter.repository.NewsletterEntity;

public interface NewsletterRepository {

	NewsletterEntity save(String name, String domain, String mailingList, String status);

	Optional<NewsletterEntity> findByDomainOrMailingList(String domain, String mailingList);

	List<NewsletterEntity> findNewsLetterListByNameOrCategoryId(String name, UUID categoryId);

	NewsletterEntity getById(UUID id);

	List<NewsletterEntity> getNewsLetterListByCategoryIdList(List<UUID> categoryIdList);
}
