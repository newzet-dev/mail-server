package com.newzet.api.subscription.business.service;

import java.util.UUID;

import com.newzet.api.subscription.controller.dto.SubscriptionWithImageListResponse;

public interface SubscriptionQueryRepository {
	boolean isSubscribed(UUID userId, String fromDomain, String mailingList);

	SubscriptionWithImageListResponse getSubscriptionWithImage(UUID userId);
}
