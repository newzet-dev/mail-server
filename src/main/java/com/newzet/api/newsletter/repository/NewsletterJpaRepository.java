package com.newzet.api.newsletter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsletterJpaRepository extends JpaRepository<NewsletterEntity, UUID> {
	Optional<NewsletterEntity> findNewsletterByDomainOrMailingList(String domain,
		String mailingList);

	List<NewsletterEntity> findNewsletterListByName(String name);

	List<NewsletterEntity> findNewsletterListByCategoryId(UUID categoryId);

	@Query("select n from NewsletterEntity n where n.category.id in :categoryIdList")
	List<NewsletterEntity> findByCategoryIdList(List<UUID> categoryIdList);

	@Query("select n.imageUrl from NewsletterEntity n"
		+ " where n.domain in :domain or n.mailingList in :mailingList")
	String getImageUrlByDomainOrMailingList(@Param("domain") String domain,
		@Param("mailingList") String mailingList);
}
