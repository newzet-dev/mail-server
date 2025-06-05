package com.newzet.api.fcm.business;

import com.newzet.api.fcm.domain.FcmToken;

import java.util.Optional;
import java.util.UUID;

public interface FcmTokenRepository {
    Optional<FcmToken> findByTokenValue(String value);

    boolean deleteFcmToken(UUID userId, String value);

    FcmToken save(FcmToken fcmToken);
}
