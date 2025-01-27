package com.newzet.api.newsletter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsletterJpaRepository extends JpaRepository<NewsletterJpaEntity, Long> {
	Optional<NewsletterJpaEntity> findNewsletterByDomainOrMaillingList(String domain,
		String maillingList);
}
