package com.newzet.api.newsletter.presentation.dto;

import java.util.UUID;

public record NewsletterInfoResponse(
	UUID id,
	String name,
	String imageUrl,
	String detail,
	String status,
	String dayOfWeek,
	String subscriptionUrl,
	boolean isSubscribing,
	String categoryName
) {
}
