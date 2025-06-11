package com.newzet.api.subscription.business.repository;

import java.util.UUID;

import com.newzet.api.subscription.domain.Subscription;

public interface SubscriptionRepository {
	Subscription save(UUID userId, String newsletterName, String newsletterDomain, String newsletterMailingList);

	void delete(UUID subscriptionId);
}
