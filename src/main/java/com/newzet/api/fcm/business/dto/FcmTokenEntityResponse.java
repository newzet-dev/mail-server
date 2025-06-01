package com.newzet.api.fcm.business.dto;

import com.newzet.api.fcm.domain.FcmToken;

import java.time.LocalDateTime;
import java.util.UUID;

public record FcmTokenEntityResponse(
        UUID id,
        UUID userId,
        String value,
        LocalDateTime createdAt
) {

    public FcmToken toDomain() {
        return FcmToken.create(id, userId, value, createdAt);
    }
}


