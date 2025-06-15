package com.newzet.api.subscription.repository.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SUBSCRIPTIONS",
	indexes = {
		@Index(name = "idx_subscriptions_user_id", columnList = "user_id")
	})
public class SubscriptionEntity {
	@Id
	@UuidGenerator
	@Column(columnDefinition = "UUID", updatable = false, nullable = false)
	private UUID id;

	@Column(columnDefinition = "UUID", nullable = false)
	private UUID userId;

	@Column(nullable = false)
	private String newsletterName;

	@Column(nullable = false)
	private String newsletterDomain;

	@Column
	private String newsletterMailingList;

	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT NOW()", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private LocalDateTime deletedAt;

	public static SubscriptionEntity create(UUID userId, String newsletterName,
		String newsletterDomain, String newsletterMailingList) {
		return SubscriptionEntity.builder()
			.userId(userId)
			.newsletterName(newsletterName)
			.newsletterDomain(newsletterDomain)
			.newsletterMailingList(newsletterMailingList)
			.createdAt(LocalDateTime.now())
			.build();
	}

	public SubscriptionEntityDto toEntityDto() {
		return SubscriptionEntityDto.create(id, userId, newsletterName, newsletterDomain,
			newsletterMailingList);
	}
}
