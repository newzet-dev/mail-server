package com.newzet.api.subscription.business.service;

import java.util.UUID;

public interface SubscriptionQueryRepository {
	boolean isSubscribed(UUID userId, String fromDomain, String mailingList);

}
