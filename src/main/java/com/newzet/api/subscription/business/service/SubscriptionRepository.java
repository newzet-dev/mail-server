package com.newzet.api.subscription.business.service;

import java.util.Optional;
import java.util.UUID;

import com.newzet.api.newsletter.repository.NewsletterEntity;
import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;
import com.newzet.api.user.business.dto.UserEntityDto;

public interface SubscriptionRepository {
	SubscriptionEntityDto create(UserEntityDto userDto, NewsletterEntity newsletterEntity);

	Optional<SubscriptionEntityDto> findByUserIdAndNewsletterId(UUID userId, UUID newsletterId);

	void save(SubscriptionEntityDto subscriptionDto);

	SubscriptionEntityDto getById(UUID id);
}
