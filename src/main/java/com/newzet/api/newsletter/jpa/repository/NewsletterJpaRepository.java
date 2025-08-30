package com.newzet.api.newsletter.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.newzet.api.newsletter.jpa.entity.NewsletterEntity;

public interface NewsletterJpaRepository extends JpaRepository<NewsletterEntity, UUID> {

	List<NewsletterEntity> findNewsletterListByNameStartingWith(String name);

	List<NewsletterEntity> findNewsletterListByCategoryId(UUID categoryId);

	@Query("select n from NewsletterEntity n where n.categoryId in :categoryIdList")
	List<NewsletterEntity> findByCategoryIdList(List<UUID> categoryIdList);

	@Query("select n from NewsletterEntity n where n.id in :idList")
	List<NewsletterEntity> findByIdList(List<UUID> idList);

	@Query("select n.imageUrl from NewsletterEntity n"
		+ " where n.domain in :domain or n.mailingList in :mailingList")
	String findImageUrlByDomainOrMailingList(@Param("domain") String domain,
		@Param("mailingList") String mailingList);
}
