package com.newzet.api.newsletter.jpa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.newzet.api.newsletter.domain.NewsletterColor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "NEWSLETTER")
public class NewsletterEntity {
	@Id
	@UuidGenerator
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "category_id", nullable = false)
	private UUID categoryId;

	@Column(name = "domain", unique = true, nullable = false)
	private String domain;

	@Column(name = "mailing_list")
	private String mailingList;

	@Column(name = "priority", nullable = false)
	private int priority;

	@Column(name = "image_url", nullable = false)
	private String imageUrl;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "detail", nullable = false)
	private String detail;

	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "day_of_week")
	private String dayOfWeek;

	@Column(name = "subscription_url", nullable = false)
	private String subscriptionUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "color", nullable = false)
	private NewsletterColor color = NewsletterColor.DEFAULT;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}
