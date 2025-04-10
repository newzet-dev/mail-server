package com.newzet.api.subscription.business.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;
import com.newzet.api.subscription.domain.Subscription;
import com.newzet.api.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionService {

	private final SubscriptionRepository subscriptionRepository;

	public void addSubscription(User user, Newsletter newsletter) {
		subscriptionRepository.findByUserIdAndNewsletterId(
				user.getId(), newsletter.getId())
			.ifPresentOrElse(entityDto -> {
				Subscription activatedSubscription = entityDto.toDomain().reactivate();
				subscriptionRepository.save(activatedSubscription.toEntityDto(entityDto.getId()));
			}, () -> {
				subscriptionRepository.create(user.toEntityDto(), newsletter.toEntityDto());
			});
	}

	public void deleteSubscription(UUID subscriptionId) {
		SubscriptionEntityDto entityDto = subscriptionRepository.getById(subscriptionId);
		Subscription deletedSubscription = entityDto.toDomain().unsubscribe();
		subscriptionRepository.save(deletedSubscription.toEntityDto(entityDto.getId()));
	}
}
