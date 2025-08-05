package com.newzet.api.subscription.business.repository;

import java.util.List;
import java.util.UUID;

import com.newzet.api.subscription.domain.Subscription;
import com.newzet.api.subscription.jpa.dto.SubscriptionListWithImageProjection;

public interface SubscriptionRepository {
	Subscription save(Subscription subscription);

	List<SubscriptionListWithImageProjection> getSubscriptionWithImage(UUID userId);

	void delete(UUID subscriptionId);
}
