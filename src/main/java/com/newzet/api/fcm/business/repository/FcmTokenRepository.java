package com.newzet.api.fcm.business.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.newzet.api.fcm.domain.FcmToken;

public interface FcmTokenRepository {
	Optional<FcmToken> findIfExistByValue(String value);

	boolean deleteFcmToken(FcmToken fcmToken);

	FcmToken save(FcmToken fcmToken);

	FcmToken findByUserIdAndValue(UUID userId, String value);

	List<FcmToken> findAllByUserId(UUID userId);
}
