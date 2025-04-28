package com.newzet.api.newsletter.business.dto;

import java.util.UUID;

import com.newzet.api.category.business.dto.CategoryEntityDto;
import com.newzet.api.newsletter.domain.Color;
import com.newzet.api.newsletter.domain.Newsletter;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NewsletterEntityDto {
	private final UUID id;
	private final String name;
	private final CategoryEntityDto category;
	private final String domain;
	private final String mailingList;
	private final Integer priority;
	private final String imageUrl;
	private final String description;
	private final String detail;
	private final String status;
	private final String dayOfWeek;
	private final String subscriptionUrl;
	private final Color color;

	public static NewsletterEntityDto create(UUID id, String name, CategoryEntityDto category,
		String domain,
		String mailingList, Integer priority, String imageUrl, String description,
		String detail, String status, String dayOfWeek, String subscriptionUrl, Color color) {
		return NewsletterEntityDto.builder()
			.id(id)
			.name(name)
			.category(category)
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

	public Newsletter toDomain() {
		return Newsletter.create(id, name, category.toDomain(), domain,
			mailingList, priority, imageUrl, description, detail, status, dayOfWeek,
			subscriptionUrl, color);
	}

}