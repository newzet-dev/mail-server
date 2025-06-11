package com.newzet.api.newsletter.business.dto;

import java.util.UUID;

import com.newzet.api.newsletter.domain.NewsletterColor;

public record NewsletterSaveRequest(
	String name,
	UUID categoryId,
	String domain,
	String mailingList,
	int priority,
	String imageUrl,
	String description,
	String detail,
	String status,
	String dayOfWeek,
	String subscriptionUrl,
	NewsletterColor color
) {
}
