package com.newzet.api.subscription.business.repository;

import java.util.UUID;

public interface SubscriptionQueryRepository {
	boolean isSubscribed(UUID userId, String fromDomain, String mailingList);
}
