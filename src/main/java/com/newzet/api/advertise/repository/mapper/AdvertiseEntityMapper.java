package com.newzet.api.advertise.repository.mapper;

import com.newzet.api.advertise.domain.Advertise;
import com.newzet.api.advertise.repository.entity.AdvertiseEntity;

public class AdvertiseEntityMapper {

	public static Advertise toDomain(AdvertiseEntity entity) {
		return new Advertise(entity.getId(), entity.getNewsletterId());
	}
}
