package com.newzet.api.newsletter.controller.dto;

import java.util.List;

public record NewsletterListResponse(
	List<NewsletterResponse> newsletterList
) {
	public static NewsletterListResponse create(List<NewsletterResponse> newsletterList) {
		return new NewsletterListResponse(newsletterList);
	}
}
