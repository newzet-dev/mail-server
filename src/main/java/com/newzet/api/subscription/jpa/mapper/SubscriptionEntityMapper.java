package com.newzet.api.subscription.jpa.mapper;

import com.newzet.api.subscription.domain.Subscription;
import com.newzet.api.subscription.jpa.entity.SubscriptionEntity;

public class SubscriptionEntityMapper {
	public static Subscription toDomain(SubscriptionEntity entity) {
		return new Subscription(entity.getId(), entity.getUserId(), entity.getNewsletterName(),
			entity.getNewsletterDomain(), entity.getNewsletterMailingList(), entity.getCreatedAt(),
			entity.getDeletedAt());
	}

	public static SubscriptionEntity toEntity(Subscription domain) {
		return new SubscriptionEntity(domain.getId(), domain.getUserId(), domain.getNewsletterName(),
			domain.getNewsletterDomain(), domain.getNewsletterMailingList(), domain.getCreatedAt(),
			domain.getDeletedAt());
	}
}
