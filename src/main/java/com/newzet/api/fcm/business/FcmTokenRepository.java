package com.newzet.api.fcm.business;

import java.util.UUID;

import com.newzet.api.fcm.business.dto.FcmTokenEntityResponse;

public interface FcmTokenRepository {
	FcmTokenEntityResponse createIfAbsent(UUID userId, String value);

	Boolean deleteFcmToken(UUID userId, String value);
}
