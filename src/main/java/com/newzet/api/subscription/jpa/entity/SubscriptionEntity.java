package com.newzet.api.subscription.jpa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SUBSCRIPTION")
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

	public static SubscriptionEntity create(UUID userId, String newsletterName, String newsletterDomain,
		String newsletterMailingList) {
		return new SubscriptionEntity(null, userId, newsletterName, newsletterDomain, newsletterMailingList,
			LocalDateTime.now(), null);
	}
}
