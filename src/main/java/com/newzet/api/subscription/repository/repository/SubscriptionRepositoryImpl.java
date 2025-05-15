package com.newzet.api.subscription.repository.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;
import com.newzet.api.subscription.business.service.SubscriptionRepository;
import com.newzet.api.subscription.repository.entity.SubscriptionEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

	private final SubscriptionJpaRepository subscriptionJpaRepository;

	@Override
	public SubscriptionEntityDto save(UUID userId, String newsletterName, String newsletterDomain, String newsletterMailingList) {
		SubscriptionEntity subscriptionEntity = SubscriptionEntity.
			create(userId, newsletterName, newsletterDomain, newsletterMailingList);
		return subscriptionJpaRepository.save(subscriptionEntity).toEntityDto();
	}
}
