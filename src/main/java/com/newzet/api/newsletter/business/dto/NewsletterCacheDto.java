package com.newzet.api.newsletter.business.dto;

import java.util.UUID;

import com.newzet.api.newsletter.domain.NewsletterStatus;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NewsletterCacheDto {
	private final UUID id;
	private final String name;
	private final String categoryName;
	private final String domain;
	private final String mailingList;
	private final Integer priority;
	private final String imageUrl;
	private final String description;
	private final String detail;
	private final String status;
	private final String dayOfWeek;
	private final String subscriptionUrl;
	private final String color;

	public static NewsletterCacheDto create(UUID id, String name, String categoryName, String domain,
		String mailingList, Integer priority, String imageUrl, String description,
		String detail, String status, String dayOfWeek, String subscriptionUrl, String color) {
		return NewsletterCacheDto.builder()
			.id(id)
			.name(name)
			.categoryName(categoryName)
			.domain(domain)
			.mailingList(mailingList)
			.priority(priority)
			.imageUrl(imageUrl)
			.description(description)
			.detail(detail)
			.status(status)
			.dayOfWeek(dayOfWeek)
			.subscriptionUrl(subscriptionUrl)
			.color(color)
			.build();
	}
}
