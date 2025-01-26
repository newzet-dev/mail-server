package com.newzet.api.newsletter;

import java.util.List;

public interface NewsletterJpaRepositoryCustom {
	List<NewsletterJpaEntity> findAllByUserId(Long userId);
}
