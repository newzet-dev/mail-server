package com.newzet.api.newsletter.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record Newsletter(
	UUID id,
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
	NewsletterColor color,
	LocalDateTime deletedAt
) {
}
