package com.newzet.api.subscription.business.dto;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SubscriptionEntityDto {
	private final UUID id;
	private final UUID userId;
	private final String newsletterName;
	private final String newsletterDomain;
	private final String newsletterMailingList;

	public static SubscriptionEntityDto create(UUID id, UUID userId, String newsletterName, String newsletterDomain, String newsletterMailingList) {
		return SubscriptionEntityDto.builder()
			.id(id)
			.userId(userId)
			.newsletterName(newsletterName)
			.newsletterDomain(newsletterDomain)
			.newsletterMailingList(newsletterMailingList)
			.build();
	}
}
