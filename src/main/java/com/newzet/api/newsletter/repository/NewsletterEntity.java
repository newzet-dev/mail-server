package com.newzet.api.newsletter.repository;

import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(unique = true, nullable = false)
	private String domain;

	private String mailingList;

	@Enumerated(EnumType.STRING)
	private NewsletterEntityStatus status = NewsletterEntityStatus.UNREGISTERED;

	public static NewsletterEntity create(String name, String domain, String mailingList,
		String status) {
		return NewsletterEntity.builder()
			.name(name)
			.domain(domain)
			.mailingList(mailingList)
			.status(NewsletterEntityStatus.valueOf(status))
			.build();
	}

	public static NewsletterEntity create(Long id, String name, String domain, String mailingList,
		String status) {
		return NewsletterEntity.builder()
			.id(id)
			.name(name)
			.domain(domain)
			.mailingList(mailingList)
			.status(NewsletterEntityStatus.valueOf(status))
			.build();
	}

	public NewsletterEntityDto toEntityDto() {
		return NewsletterEntityDto.create(id, name, domain, mailingList, status.name());
	}
}
