package com.newzet.api.subscription.business.service;

import java.util.UUID;

public interface SubscriptionQueryRepository {
	boolean isSubscribe(UUID userId, String fromDomain, String mailingList);
}
