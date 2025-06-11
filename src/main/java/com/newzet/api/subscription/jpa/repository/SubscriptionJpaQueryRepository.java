package com.newzet.api.subscription.jpa.repository;

import static com.newzet.api.newsletter.jpa.entity.QNewsletterEntity.*;
import static com.newzet.api.subscription.jpa.entity.QSubscriptionEntity.*;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.subscription.business.repository.SubscriptionQueryRepository;
import com.newzet.api.subscription.presentation.dto.SubscriptionListWithImageResponse;
import com.newzet.api.subscription.presentation.dto.SubscriptionWithImageResponse;
import com.querydsl.core.types.Projections;
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

	@Override
	public SubscriptionListWithImageResponse getSubscriptionWithImage(UUID userId) {
		return SubscriptionListWithImageResponse.of(queryFactory
			.select(Projections.constructor(SubscriptionWithImageResponse.class,
				subscriptionEntity.id,
				subscriptionEntity.newsletterName,
				subscriptionEntity.newsletterDomain,
				newsletterEntity.imageUrl,
				newsletterEntity.status,
				newsletterEntity.dayOfWeek))
			.from(subscriptionEntity)
			.leftJoin(newsletterEntity)
			.on(newsletterEntity.domain.eq(subscriptionEntity.newsletterDomain))
			.fetch());
	}

}
