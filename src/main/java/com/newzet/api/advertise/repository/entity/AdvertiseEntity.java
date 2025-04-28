package com.newzet.api.advertise.repository.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.newzet.api.advertise.business.dto.AdvertiseEntityDto;
import com.newzet.api.newsletter.repository.NewsletterEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdvertiseEntity {
	@Id
	@UuidGenerator
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "newsletter_id")
	private NewsletterEntity newsletter;

	public static AdvertiseEntity create(NewsletterEntity newsletter) {
		return new AdvertiseEntity(null, newsletter);
	}

	public static AdvertiseEntity create(UUID id, NewsletterEntity newsletter) {
		return new AdvertiseEntity(id, newsletter);
	}

	public AdvertiseEntityDto toEntityDto() {
		return AdvertiseEntityDto.create(id, newsletter.toEntityDto());
	}
}
