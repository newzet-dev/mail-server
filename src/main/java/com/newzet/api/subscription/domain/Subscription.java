package com.newzet.api.subscription.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Subscription {
	private final LocalDateTime createdAt;
	private final LocalDateTime deletedAt;

	public static Subscription create(LocalDateTime createdAt,
		LocalDateTime deletedAt) {
		return new Subscription(createdAt, deletedAt);
	}

	public SubscriptionEntityDto toEntityDto(UUID id) {
		return SubscriptionEntityDto.create(id, createdAt, deletedAt);
	}

	public Subscription reactivate() {
		return Subscription.create(createdAt, null);
	}

	public Subscription unsubscribe() {
		return Subscription.create(createdAt, LocalDateTime.now());
	}
}
