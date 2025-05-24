package com.newzet.api.subscription.controller.dto;

import java.util.List;

public record SubscriptionWithImageListResponse(
	List<SubscriptionWithImageResponse> subscriptionList
) {

	public static SubscriptionWithImageListResponse of(List<SubscriptionWithImageResponse> subscriptionList) {
		return new SubscriptionWithImageListResponse(subscriptionList);
	}
}
