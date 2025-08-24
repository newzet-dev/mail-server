package com.newzet.api.subscription.jpa.repository;

import static com.newzet.api.newsletter.jpa.entity.QNewsletterEntity.*;
import static com.newzet.api.subscription.jpa.entity.QSubscriptionEntity.*;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.subscription.business.repository.SubscriptionQueryRepository;
import com.newzet.api.subscription.jpa.dto.SubscriptionListWithImageProjection;
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

	List<SubscriptionListWithImageProjection> getSubscriptionWithImage(UUID userId) {
		return queryFactory
			.select(Projections.constructor(SubscriptionListWithImageProjection.class,
				subscriptionEntity.id,
				subscriptionEntity.newsletterName,
				newsletterEntity.domain,
				newsletterEntity.imageUrl,
				newsletterEntity.status,
				newsletterEntity.dayOfWeek
			))
			.from(subscriptionEntity)
			.innerJoin(newsletterEntity)
			.on(subscriptionEntity.newsletterDomain.eq(newsletterEntity.domain))
			.where(subscriptionEntity.userId.eq(userId))
			.fetch();
	}
}
