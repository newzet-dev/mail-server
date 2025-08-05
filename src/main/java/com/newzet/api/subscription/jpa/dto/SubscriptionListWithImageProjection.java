package com.newzet.api.subscription.jpa.dto;

import java.util.UUID;

public interface SubscriptionListWithImageProjection {
	UUID getId();

	String getNewsletterName();

	String getDomain();

	String getImageUrl();

	String getStatus();

	String getDayOfWeek();
}
