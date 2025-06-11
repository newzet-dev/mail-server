package com.newzet.api.newsletter.presentation.mapper;

import java.util.List;

import com.newzet.api.category.domain.Category;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.presentation.dto.NewsletterInfoResponse;
import com.newzet.api.newsletter.presentation.dto.NewsletterListResponse;
import com.newzet.api.newsletter.presentation.dto.NewsletterRecommendResponse;
import com.newzet.api.newsletter.presentation.dto.NewsletterResponse;

public class NewsletterResponseMapper {

	public static NewsletterListResponse toListResponse(List<Newsletter> newsletterList) {
		List<NewsletterResponse> newsletterResponseList = newsletterList.stream()
			.map(NewsletterResponseMapper::toResponse)
			.toList();
		return new NewsletterListResponse(newsletterResponseList);
	}

	public static NewsletterResponse toResponse(Newsletter newsletter) {
		return new NewsletterResponse(newsletter.id(), newsletter.name(), newsletter.imageUrl(),
			newsletter.description(), newsletter.priority());
	}

	public static NewsletterInfoResponse toInfoResponse(Newsletter newsletter, Category category,
		boolean isSubscribing) {
		return new NewsletterInfoResponse(newsletter.id(), newsletter.name(), newsletter.imageUrl(),
			newsletter.detail(), newsletter.status(), newsletter.dayOfWeek(), newsletter.subscriptionUrl(),
			isSubscribing,
			category.name());

	}

	public static NewsletterRecommendResponse toRecommendResponse(List<Newsletter> newsletterList) {
		List<NewsletterResponse> newsletterResponse = newsletterList.stream()
			.map(NewsletterResponseMapper::toResponse)
			.toList();
		return new NewsletterRecommendResponse(newsletterResponse);
	}
}
