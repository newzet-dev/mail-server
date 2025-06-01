package com.newzet.api.subscription.controller.dto;

import java.util.UUID;

public class SubscriptionWithImageResponse {

	private String subscriptionId;
	private String newsletterName;
	private String newsletterDomain;
	private String newsletterImageUrl;
	private String newsletterStatus;
	private String newsletterDayOfWeek;

	public SubscriptionWithImageResponse(UUID subscriptionId, String newsletterName,
		String newsletterDomain, String newsletterImageUrl, String newsletterStatus,
		String newsletterDayOfWeek) {
		this.subscriptionId = subscriptionId.toString();
		this.newsletterName = newsletterName;
		this.newsletterDomain = newsletterDomain;
		this.newsletterImageUrl = newsletterImageUrl;
		this.newsletterStatus = newsletterStatus;
		this.newsletterDayOfWeek = newsletterDayOfWeek;
	}
}
