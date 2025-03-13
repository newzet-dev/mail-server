package com.newzet.api.subscription.business.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.newzet.api.subscription.domain.Subscription;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SubscriptionEntityDto {
	private final UUID id;
	private final LocalDateTime createdAt;
	private final LocalDateTime deletedAt;

	public static SubscriptionEntityDto create(UUID id,
		LocalDateTime createdAt, LocalDateTime deletedAt) {
		return SubscriptionEntityDto.builder()
			.id(id)
			.createdAt(createdAt)
			.deletedAt(deletedAt)
			.build();
	}

	public Subscription toDomain() {
		return Subscription.create(createdAt, deletedAt);
	}
}
