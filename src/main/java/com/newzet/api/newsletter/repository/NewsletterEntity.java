package com.newzet.api.newsletter.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.newzet.api.category.repository.CategoryEntity;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.domain.Color;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "NEWSLETTERS")
// @Table(name = "NEWSLETTER", indexes = {
// 	@Index(name = "unique_non_null_mailing_list", columnList = "mailingList")})
public class NewsletterEntity {
	@Id
	@UuidGenerator
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private CategoryEntity category;
	// join, index 조회, 정합성...

	@Column(unique = true, nullable = false)
	private String domain;

	private String mailingList;

	private Integer priority;

	private String imageUrl;

	private String description;

	private String detail;

	private String status;

	private String dayOfWeek;

	private String subscriptionUrl;

	@Enumerated(EnumType.STRING)
	private Color color;

	private LocalDateTime deletedAt;

	public static NewsletterEntity create(String name, String domain, String mailingList,
		String status) {
		return NewsletterEntity.builder()
			.name(name)
			.domain(domain)
			.mailingList(mailingList)
			.status(NewsletterEntityStatus.valueOf(status).toString())
			.build();
	}

	public static NewsletterEntity create(String name, CategoryEntity category, String domain,
		String mailingList, Integer priority, String imageUrl, String description,
		String detail, String status, String dayOfWeek, String subscriptionUrl, Color color) {
		return NewsletterEntity.builder()
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

	public static NewsletterEntity create(UUID id, String name, CategoryEntity category,
		String domain, String mailingList, Integer priority, String imageUrl, String description,
		String detail, String status, String dayOfWeek, String subscriptionUrl, Color color,
		LocalDateTime deletedAt) {
		return NewsletterEntity.builder()
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
			.deletedAt(deletedAt)
			.build();
	}

	public NewsletterEntityDto toEntityDto() {
		return NewsletterEntityDto.create(id, name, category.toEntityDto(), domain,
			mailingList, priority, imageUrl, description, detail, status, dayOfWeek,
			subscriptionUrl, color);
	}
}
