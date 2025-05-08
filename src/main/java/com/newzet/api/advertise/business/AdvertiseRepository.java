package com.newzet.api.advertise.business;

import java.util.List;

import com.newzet.api.advertise.business.dto.AdvertiseEntityDto;

public interface AdvertiseRepository {

	List<AdvertiseEntityDto> getAllAdvertise();
}
