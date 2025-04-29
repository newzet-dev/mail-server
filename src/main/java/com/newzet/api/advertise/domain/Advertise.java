package com.newzet.api.advertise.domain;

import java.util.UUID;

import com.newzet.api.advertise.business.dto.AdvertiseEntityDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Advertise {
	private final UUID id;
	private final UUID newsletterId;

	public static Advertise create(UUID id, UUID newsletterId) {
		return new Advertise(id, newsletterId);
	}

	public AdvertiseEntityDto toEntityDto() {
		return AdvertiseEntityDto.create(id, newsletterId);
	}
}
