package com.newzet.api.subscription.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record Subscription(
	UUID id,
	UUID userId,
	String newsletterName,
	String newsletterDomain,
	String newsletterMailingList,
	LocalDateTime createdAt,
	LocalDateTime deletedAt
) {
}
