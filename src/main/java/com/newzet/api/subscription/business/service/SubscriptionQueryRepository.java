package com.newzet.api.subscription.business.service;

import java.util.UUID;

import com.newzet.api.subscription.controller.dto.SubscriptionListWithImageResponse;

public interface SubscriptionQueryRepository {
	boolean isSubscribed(UUID userId, String fromDomain, String mailingList);

	SubscriptionListWithImageResponse getSubscriptionWithImage(UUID userId);
}
