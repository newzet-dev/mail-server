package com.newzet.api.subscription.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.subscription.business.repository.SubscriptionRepository;
import com.newzet.api.subscription.domain.Subscription;
import com.newzet.api.subscription.jpa.dto.SubscriptionListWithImageProjection;
import com.newzet.api.subscription.jpa.entity.SubscriptionEntity;
import com.newzet.api.subscription.jpa.exception.NoSubscriptionException;
import com.newzet.api.subscription.jpa.mapper.SubscriptionEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

	private final SubscriptionJpaRepository subscriptionJpaRepository;
	private final SubscriptionJpaQueryRepository subscriptionJpaQueryRepository;

	@Override
	public Subscription save(Subscription subscription) {
		SubscriptionEntity subscriptionEntity = SubscriptionEntityMapper.toEntity(subscription);
		return SubscriptionEntityMapper.toDomain(
			subscriptionJpaRepository.save(subscriptionEntity));
	}

	@Override
	public List<SubscriptionListWithImageProjection> getSubscriptionWithImage(UUID userId) {
		return subscriptionJpaQueryRepository.getSubscriptionWithImage(userId);
	}

	@Override
	public void delete(UUID subscriptionId) {
		subscriptionJpaRepository.delete(
			subscriptionJpaRepository.findById(subscriptionId)
				.orElseThrow(() -> new NoSubscriptionException("해당 id의 구독 상태가 존재하지 않습니다.")));
	}
}
