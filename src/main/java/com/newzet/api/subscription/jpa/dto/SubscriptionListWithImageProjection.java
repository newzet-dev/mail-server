package com.newzet.api.subscription.jpa.dto;

import java.util.UUID;

public record SubscriptionListWithImageProjection(
	UUID id,
	String newsletterName,
	String domain,
	String imageUrl,
	String status,
	String dayOfWeek
) {
}
