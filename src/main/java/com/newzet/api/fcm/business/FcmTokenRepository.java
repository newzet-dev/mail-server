package com.newzet.api.fcm.business;

import java.util.List;
import java.util.UUID;

import com.newzet.api.category.business.dto.CategoryEntityDto;
import com.newzet.api.fcm.business.dto.FcmTokenEntityResponse;
import com.newzet.api.fcm.domain.FcmToken;

public interface FcmTokenRepository {
	FcmTokenEntityResponse create(UUID userId, String value);
}
