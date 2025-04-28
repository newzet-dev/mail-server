package com.newzet.api.advertise.business;

import java.util.List;
import java.util.UUID;

public interface AdvertiseRepository {

	List<UUID> getAdvertiseNewsletterIdList();
}
