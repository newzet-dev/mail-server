package com.newzet.api.advertise.business.dto;

import java.util.UUID;

import com.newzet.api.advertise.domain.Advertise;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdvertiseEntityDto {
	private final UUID id;
	private final UUID newsletterId;

	public static AdvertiseEntityDto create(UUID id, UUID newsletterId) {
		return new AdvertiseEntityDto(id, newsletterId);
	}

	public Advertise toDomain() {
		return Advertise.create(id, newsletterId);
	}
}
