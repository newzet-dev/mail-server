package com.newzet.api.newsletter.controller.dto;

import java.util.UUID;

public record NewsletterInfoResponse(
	String id,
	String name,
	String imageUrl,
	String detail,
	String status,
	String dayOfWeek,
	String subscriptionUrl,
	Boolean isSubscribing,
	String categoryName
) {
	public static NewsletterInfoResponse create(UUID newsletterId, String name, String imageUrl,
		String detail, String status, String dayOfWeek, String subscriptionUrl,
		Boolean isSubscribing, String categoryName) {
		return new NewsletterInfoResponse(newsletterId.toString(), name, imageUrl, detail,
			status, dayOfWeek, subscriptionUrl, isSubscribing, categoryName);
	}
}
