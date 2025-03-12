package com.newzet.api.subscription.business.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SubscriptionEntityDto {
	private final UUID id;
	private final NewsletterEntityDto newsletterEntityDto;
	private final LocalDateTime createdAt;

	public static SubscriptionEntityDto create(UUID id, NewsletterEntityDto newsletterEntityDto,
		LocalDateTime createdAt) {
		return SubscriptionEntityDto.builder()
			.id(id)
			.newsletterEntityDto(newsletterEntityDto)
			.createdAt(createdAt)
			.build();
	}
}
