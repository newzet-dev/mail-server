package com.newzet.api.newsletter.domain;

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
	NewsletterColor color
) {

	public static Newsletter create(String name, UUID categoryId, String domain,
		String mailingList, Integer priority, String imageUrl, String description,
		String detail, String status, String dayOfWeek, String subscriptionUrl, NewsletterColor color) {
		return new Newsletter(null, name, categoryId, domain, mailingList, priority, imageUrl, description, detail,
			status, dayOfWeek, subscriptionUrl, color);
	}
}
