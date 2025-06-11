package com.newzet.api.subscription.business.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.common.util.UuidConverter;
import com.newzet.api.subscription.business.repository.SubscriptionQueryRepository;
import com.newzet.api.subscription.business.repository.SubscriptionRepository;

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

	public void deleteSubscription(String subscriptionId) {
		UUID convertedSubscriptionId = UuidConverter.convert(subscriptionId);
		subscriptionRepository.delete(convertedSubscriptionId);
	}

	public boolean isSubscribing(UUID userId, String domain, String mailingList) {
		return subscriptionQueryRepository.isSubscribed(userId, domain, mailingList);
	}
}
