package com.newzet.api.subscription.business.service;

import java.util.List;
import java.util.UUID;

import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;
import com.newzet.api.subscription.repository.repository.dto.SubscriptionListWithImageProjection;

public interface SubscriptionRepository {
	SubscriptionEntityDto save(UUID userId, String newsletterName, String newsletterDomain,
		String newsletterMailingList);

	List<SubscriptionListWithImageProjection> getSubscriptionWithImage(UUID userId);

	void delete(UUID subscriptionId);
}
