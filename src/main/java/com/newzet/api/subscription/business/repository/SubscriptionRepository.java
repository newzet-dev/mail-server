package com.newzet.api.subscription.business.repository;

import java.util.UUID;

import com.newzet.api.subscription.domain.Subscription;

public interface SubscriptionRepository {
	Subscription save(Subscription subscription);

	void delete(UUID subscriptionId);
}
