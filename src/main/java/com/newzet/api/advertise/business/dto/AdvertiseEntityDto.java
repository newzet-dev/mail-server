package com.newzet.api.advertise.business.dto;

import java.util.UUID;

import com.newzet.api.advertise.domain.Advertise;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdvertiseEntityDto {
	private final UUID id;
	private final NewsletterEntityDto newsletter;

	public static AdvertiseEntityDto create(UUID id, NewsletterEntityDto newsletter) {
		return new AdvertiseEntityDto(id, newsletter);
	}

	public Advertise toDomain() {
		return Advertise.create(id, newsletter.toDomain());
	}
}
