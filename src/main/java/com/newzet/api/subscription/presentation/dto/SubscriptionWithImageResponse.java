package com.newzet.api.subscription.presentation.dto;

import java.util.UUID;

public record SubscriptionWithImageResponse(
	UUID subscriptionId,
	String newsletterName,
	String newsletterDomain,
	String newsletterImageUrl,
	String newsletterStatus,
	String newsletterDayOfWeek
) {
}
