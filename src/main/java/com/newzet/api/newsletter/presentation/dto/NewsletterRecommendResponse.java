package com.newzet.api.newsletter.presentation.dto;

import java.util.List;

public record NewsletterRecommendResponse(
	List<NewsletterResponse> newletterRecommendList
) {

	public static NewsletterRecommendResponse create(List<NewsletterResponse> newletterList) {
		return new NewsletterRecommendResponse(newletterList);
	}
}
