package com.newzet.api.newsletter.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.newzet.api.newsletter.jpa.entity.NewsletterEntity;

public interface NewsletterJpaRepository extends JpaRepository<NewsletterEntity, UUID> {

	List<NewsletterEntity> findNewsletterListByName(String name);

	List<NewsletterEntity> findNewsletterListByCategoryId(UUID categoryId);

	@Query("select n from NewsletterEntity n where n.categoryId in :categoryIdList")
	List<NewsletterEntity> findByCategoryIdList(List<UUID> categoryIdList);
}
