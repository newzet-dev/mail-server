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
		Optional<SubscriptionEntityDto> entityDto = subscriptionRepository.findByUserIdAndNewsletterId(
			user.getId(), newsletter.getId());

		if (entityDto.isEmpty()) {
			subscriptionRepository.create(user.toEntityDto(), newsletter.toEntityDto());
		} else {
			SubscriptionEntityDto dto = entityDto.get();
			Subscription activatedSubscription = dto.toDomain().reactivate();
			subscriptionRepository.save(activatedSubscription.toEntityDto(dto.getId()));
		}
	}

	public void deleteSubscription(UUID subscriptionId) {
		SubscriptionEntityDto entityDto = subscriptionRepository.getById(subscriptionId);
		Subscription deletedSubscription = entityDto.toDomain().unsubscribe();
		subscriptionRepository.save(deletedSubscription.toEntityDto(entityDto.getId()));
	}
}
