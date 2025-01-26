package com.newzet.api.newsletter.repository;

import static com.newzet.api.newsletter.repository.QNewsletterJpaEntity.*;
import static com.newzet.api.subscription.repository.QSubscriptionJpaEntity.*;

import java.util.List;

import org.springframework.stereotype.Component;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NewsletterJpaRepositoryCustomImpl implements NewsletterJpaRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<NewsletterJpaEntity> findAllByUserId(Long userId) {
		return queryFactory
			.select(newsletterJpaEntity)
			.from(subscriptionJpaEntity)
			.join(subscriptionJpaEntity.newsletter, newsletterJpaEntity)
			.where(subscriptionJpaEntity.user.id.eq(userId))
			.fetch();
	}
}
