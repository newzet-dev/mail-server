package com.newzet.api.newsletter.domain;

import java.util.UUID;

import com.newzet.api.category.domain.Category;
import com.newzet.api.newsletter.business.dto.NewsletterCacheDto;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PROTECTED)
public class Newsletter {
	private final UUID id;
	private final String name;
	private final Category category;
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

	// TODO: 삭제 예정
	public static Newsletter create(UUID id, String name, String domain, String mailingList,
		String status) {
		return new Newsletter(id, name, null, domain, mailingList,
			null, null, null, null, null,
			null, null, null);
	}

	public static Newsletter create(UUID id, String name, Category category, String domain,
		String mailingList, Integer priority, String imageUrl, String description,
		String detail, String status, String dayOfWeek, String subscriptionUrl, Color color) {
		return Newsletter.builder()
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

	public NewsletterCacheDto toCacheDto() {
		return NewsletterCacheDto.create(id, name, category, domain,
			mailingList, priority, imageUrl, description, detail, status, dayOfWeek,
			subscriptionUrl, color);
	}

	public NewsletterEntityDto toEntityDto() {
		return NewsletterEntityDto.create(id, name, category.toEntityDto(), domain,
			mailingList, priority, imageUrl, description, detail, status, dayOfWeek,
			subscriptionUrl, color);
	}
}
