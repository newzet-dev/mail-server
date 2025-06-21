package com.newzet.api.newsletter.jpa.mapper;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.jpa.entity.NewsletterEntity;

public class NewsletterEntityMapper {
	public static NewsletterEntity toEntity(Newsletter domain) {
		return new NewsletterEntity(domain.id(), domain.name(), domain.categoryId(),
			domain.domain(), domain.mailingList(), domain.priority(), domain.imageUrl(),
			domain.description(), domain.detail(), domain.status(), domain.dayOfWeek(), domain.subscriptionUrl(),
			domain.color(), domain.deletedAt());
	}

	public static Newsletter toDomain(NewsletterEntity entity) {
		return new Newsletter(entity.getId(), entity.getName(), entity.getCategoryId(), entity.getDomain(),
			entity.getMailingList(), entity.getPriority(), entity.getImageUrl(), entity.getDescription(),
			entity.getDetail(), entity.getStatus(), entity.getDayOfWeek(), entity.getSubscriptionUrl(),
			entity.getColor(), entity.getDeletedAt());
	}
}


