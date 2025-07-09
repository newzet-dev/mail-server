package com.newzet.api.subscription.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Subscription {
	private final UUID id;
	private final UUID userId;
	private final String newsletterName;
	private final String newsletterDomain;
	private final String newsletterMailingList;
	private final LocalDateTime createdAt;
	private final LocalDateTime deletedAt;
}
