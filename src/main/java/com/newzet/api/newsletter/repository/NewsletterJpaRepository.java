package com.newzet.api.newsletter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsletterJpaRepository extends JpaRepository<NewsletterEntity, Long> {
	Optional<NewsletterEntity> findNewsletterByDomainOrMailingList(String domain,
		String mailingList);
}
