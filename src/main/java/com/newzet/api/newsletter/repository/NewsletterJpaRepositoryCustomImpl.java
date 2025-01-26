package com.newzet.api.newsletter.repository;

import static com.newzet.api.newsletter.QNewsletterJpaEntity.*;
import static com.newzet.api.subscription.QSubscriptionJpaEntity.*;

import java.util.List;

import org.springframework.stereotype.Component;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Component
public class NewsletterJpaRepositoryCustomImpl implements NewsletterJpaRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public NewsletterJpaRepositoryCustomImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

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
