package com.newzet.api.subscription.business.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.common.util.UuidConverter;
import com.newzet.api.subscription.controller.dto.SubscriptionListWithImageResponse;
import com.newzet.api.subscription.controller.dto.SubscriptionWithImageResponse;
import com.newzet.api.subscription.repository.repository.dto.SubscriptionListWithImageProjection;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionService {

	private final SubscriptionRepository subscriptionRepository;
	private final SubscriptionQueryRepository subscriptionQueryRepository;

	public void addSubscriptionIfUnsubscribed(UUID userId, String fromName, String fromDomain,
		String mailingList) {
		boolean isSubscribed = subscriptionQueryRepository.isSubscribed(userId, fromDomain,
			mailingList);
		if (!isSubscribed) {
			subscriptionRepository.save(userId, fromName, fromDomain, mailingList);
		}
	}

	public SubscriptionListWithImageResponse getSubscriptionWithImage(UUID userId) {
		List<SubscriptionListWithImageProjection> subscriptionWithImageProjection = subscriptionRepository.getSubscriptionWithImage(
			userId);
		List<SubscriptionWithImageResponse> subscriptionWithImageResponseList = subscriptionWithImageProjection.stream()
			.map(subscription -> new SubscriptionWithImageResponse(subscription.getId(),
				subscription.getNewsletterName(),
				subscription.getDomain(), subscription.getImageUrl(), subscription.getStatus(),
				subscription.getDayOfWeek()))
			.toList();

		return SubscriptionListWithImageResponse.of(subscriptionWithImageResponseList);
	}

	public void deleteSubscription(String subscriptionId) {
		UUID convertedSubscriptionId = UuidConverter.convert(subscriptionId);
		subscriptionRepository.delete(convertedSubscriptionId);
	}
}
