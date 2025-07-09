package com.newzet.api.newsletter.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Newsletter {
	private final UUID id;
	private final String name;
	private final UUID categoryId;
	private final String domain;
	private final String mailingList;
	private final int priority;
	private final String imageUrl;
	private final String description;
	private final String detail;
	private final String status;
	private final String dayOfWeek;
	private final String subscriptionUrl;
	private final NewsletterColor color;
	private final LocalDateTime deletedAt;
}
