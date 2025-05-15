package com.newzet.api.subscription.business.service;

import java.util.UUID;

import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;

public interface SubscriptionRepository {
	SubscriptionEntityDto save(UUID userId, String newsletterName, String newsletterDomain, String newsletterMailingList);
}
