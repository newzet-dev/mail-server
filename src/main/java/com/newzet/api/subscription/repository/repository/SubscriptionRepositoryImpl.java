package com.newzet.api.subscription.repository.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;
import com.newzet.api.subscription.business.service.SubscriptionRepository;
import com.newzet.api.user.business.dto.UserEntityDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

	private final SubscriptionJpaRepository subscriptionJpaRepository;

	@Override
	public SubscriptionEntityDto save(UserEntityDto userDto, NewsletterEntityDto newsletterDto) {
		return null;
	}

	@Override
	public Optional<SubscriptionEntityDto> findByUserIdAndNewsletterId(Long userId,
		Long newsletterId) {
		return null;
	}
}
