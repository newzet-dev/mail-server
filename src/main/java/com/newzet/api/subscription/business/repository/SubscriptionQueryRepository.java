package com.newzet.api.subscription.business.repository;

import java.util.UUID;

import com.newzet.api.subscription.presentation.dto.SubscriptionListWithImageResponse;

public interface SubscriptionQueryRepository {
	boolean isSubscribed(UUID userId, String fromDomain, String mailingList);

	SubscriptionListWithImageResponse getSubscriptionWithImage(UUID userId);
}
