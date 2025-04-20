package com.newzet.api.newsletter.domain;

import java.util.UUID;

import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Newsletter {
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
	private final Color color;

	public static Newsletter create(UUID id, String name, String categoryName, String domain,
		String mailingList, Integer priority, String imageUrl, String description,
		String detail, String status, String dayOfWeek, String subscriptionUrl, Color color) {
		return new Newsletter(id, name, categoryName, domain,
			mailingList, priority, imageUrl, description,
			detail, status, dayOfWeek, subscriptionUrl, color);
	}

	public NewsletterCacheDto toCacheDto() {
		return NewsletterCacheDto.create(id, name, categoryName, domain,
			mailingList, priority, imageUrl, description, detail, status, dayOfWeek,
			subscriptionUrl, color);
	}

	public NewsletterEntityDto toEntityDto() {
		return NewsletterEntityDto.create(id, name, categoryName, domain,
			mailingList, priority, imageUrl, description, detail, status, dayOfWeek,
			subscriptionUrl, color);
	}
}
