package com.newzet.api.advertise.domain;

import java.util.UUID;

import com.newzet.api.advertise.business.dto.AdvertiseEntityDto;
import com.newzet.api.newsletter.domain.Newsletter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Advertise {
	private final UUID id;
	private final Newsletter newsletter;

	public static Advertise create(UUID id, Newsletter newsletter) {
		return new Advertise(id, newsletter);
	}

	public AdvertiseEntityDto toEntityDto() {
		return AdvertiseEntityDto.create(id, newsletter.toEntityDto());
	}
}
