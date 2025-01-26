package com.newzet.api.newsletter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsletterJpaRepository
	extends JpaRepository<NewsletterJpaEntity, Long>, NewsletterJpaRepositoryCustom {
}
