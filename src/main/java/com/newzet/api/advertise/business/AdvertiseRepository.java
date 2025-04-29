package com.newzet.api.advertise.business;

import java.util.List;

import com.newzet.api.advertise.repository.entity.AdvertiseEntity;

public interface AdvertiseRepository {

	List<AdvertiseEntity> getAdvertiseNewsletterIdList();
}
