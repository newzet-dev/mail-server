package com.newzet.api.subscription.repository.repository;

import static com.newzet.api.subscription.repository.entity.QSubscriptionEntity.*;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.subscription.business.service.SubscriptionQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SubscriptionJpaQueryRepository implements SubscriptionQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public boolean isSubscribed(UUID userId, String fromDomain, String mailingList) {
		return queryFactory
			.selectOne()
			.from(subscriptionEntity)
			.where(
				(subscriptionEntity.userId.eq(userId)
					.and(subscriptionEntity.newsletterDomain.eq(fromDomain)))
					.or(subscriptionEntity.userId.eq(userId)
						.and(subscriptionEntity.newsletterMailingList.eq(mailingList)))
			)
			.fetchFirst() != null;
	}

}
