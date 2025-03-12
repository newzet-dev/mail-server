package com.newzet.api.subscription.business.service;

import java.util.Optional;

import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;
import com.newzet.api.user.business.dto.UserEntityDto;

public interface SubscriptionRepository {
	SubscriptionEntityDto save(UserEntityDto userDto, NewsletterEntityDto newsletterDto);

	Optional<SubscriptionEntityDto> findByUserIdAndNewsletterId(Long userId, Long newsletterId);
}
