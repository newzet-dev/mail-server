package com.newzet.api.subscription.repository.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;
import com.newzet.api.subscription.business.service.SubscriptionRepository;
import com.newzet.api.subscription.repository.entity.SubscriptionEntity;
import com.newzet.api.subscription.repository.exception.NoSubscriptionException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

	private final SubscriptionJpaRepository subscriptionJpaRepository;

	@Override
	public SubscriptionEntityDto save(UUID userId, String newsletterName, String newsletterDomain,
		String newsletterMailingList) {
		SubscriptionEntity subscriptionEntity = SubscriptionEntity.
			create(userId, newsletterName, newsletterDomain, newsletterMailingList);
		return subscriptionJpaRepository.save(subscriptionEntity).toEntityDto();
	}

	@Override
	public void delete(UUID subscriptionId) {
		subscriptionJpaRepository.delete(
			subscriptionJpaRepository.findById(subscriptionId)
				.orElseThrow(() -> new NoSubscriptionException("해당 id의 구독 상태가 존재하지 않습니다.")));
	}
}
