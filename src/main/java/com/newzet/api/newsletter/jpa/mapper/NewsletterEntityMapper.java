package com.newzet.api.newsletter.jpa.mapper;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.jpa.entity.NewsletterEntity;

public class NewsletterEntityMapper {
	public static NewsletterEntity toEntity(Newsletter domain) {
		return new NewsletterEntity(domain.getId(), domain.getName(), domain.getCategoryId(),
			domain.getDomain(), domain.getMailingList(), domain.getPriority(), domain.getImageUrl(),
			domain.getDescription(), domain.getDetail(), domain.getStatus(), domain.getDayOfWeek(),
			domain.getSubscriptionUrl(), domain.getColor(), domain.getDeletedAt());
	}

	public static Newsletter toDomain(NewsletterEntity entity) {
		return new Newsletter(entity.getId(), entity.getName(), entity.getCategoryId(), entity.getDomain(),
			entity.getMailingList(), entity.getPriority(), entity.getImageUrl(), entity.getDescription(),
			entity.getDetail(), entity.getStatus(), entity.getDayOfWeek(), entity.getSubscriptionUrl(),
			entity.getColor(), entity.getDeletedAt());
	}
}


