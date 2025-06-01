package com.newzet.api.fcm.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FcmToken {
    private final UUID id;
    private final UUID userId;
    private final String value;
    private final LocalDateTime createdAt;

    public static FcmToken create(UUID id, UUID userId, String value, LocalDateTime createdAt) {
        return new FcmToken(id, userId, value, createdAt);
    }
}
