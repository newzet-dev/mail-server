package com.newzet.api.subscription.controller.dto;

import java.util.List;

public record SubscriptionListWithImageResponse(
	List<SubscriptionWithImageResponse> subscriptionList
) {

	public static SubscriptionListWithImageResponse of(List<SubscriptionWithImageResponse> subscriptionList) {
		return new SubscriptionListWithImageResponse(subscriptionList);
	}
}
