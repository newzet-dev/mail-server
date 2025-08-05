package com.newzet.api.subscription.business.service;

import java.util.List;
import java.util.UUID;

import com.newzet.api.subscription.domain.Subscription;
import com.newzet.api.subscription.jpa.dto.SubscriptionListWithImageProjection;

public interface SubscriptionRepository {
	Subscription save(UUID userId, String newsletterName, String newsletterDomain,
		String newsletterMailingList);

	List<SubscriptionListWithImageProjection> getSubscriptionWithImage(UUID userId);

	void delete(UUID subscriptionId);
}
