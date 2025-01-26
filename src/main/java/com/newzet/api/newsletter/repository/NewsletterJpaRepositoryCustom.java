package com.newzet.api.newsletter.repository;

import java.util.List;

public interface NewsletterJpaRepositoryCustom {
	List<NewsletterJpaEntity> findAllByUserId(Long userId);
}
