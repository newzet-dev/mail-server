package com.newzet.api.newsletter.presentation.dto;

import java.util.List;

public record NewsletterListResponse(
	List<NewsletterResponse> newsletterList
) {
}
