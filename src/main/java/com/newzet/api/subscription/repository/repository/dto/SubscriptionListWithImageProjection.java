package com.newzet.api.subscription.repository.repository.dto;

import java.util.UUID;

public interface SubscriptionListWithImageProjection {
	UUID getId();

	String getNewsletterName();

	String getDomain();

	String getImageUrl();

	String getStatus();

	String getDayOfWeek();
}
