package com.newzet.api.newsletter.jpa.mapper;

import com.newzet.api.newsletter.business.dto.NewsletterSaveRequest;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.jpa.entity.NewsletterEntity;

public class NewsletterEntityMapper {
	public static NewsletterEntity toEntity(NewsletterSaveRequest request) {
		return NewsletterEntity.create(request.name(), request.categoryId(),
			request.domain(), request.mailingList(), request.priority(), request.imageUrl(),
			request.description(), request.detail(), request.status(), request.dayOfWeek(), request.subscriptionUrl(),
			request.color());
	}

	public static Newsletter toDomain(NewsletterEntity entity) {
		return new Newsletter(entity.getId(), entity.getName(), entity.getCategoryId(), entity.getDomain(),
			entity.getMailingList(), entity.getPriority(), entity.getImageUrl(), entity.getDescription(),
			entity.getDetail(), entity.getStatus(), entity.getDayOfWeek(), entity.getSubscriptionUrl(),
			entity.getColor());
	}
}


