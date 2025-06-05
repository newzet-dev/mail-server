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
    private final LocalDateTime createdAt;
    private UUID userId;
    private String value;

    public static FcmToken create(UUID userId, String value) {
        return new FcmToken(null, LocalDateTime.now(), userId, value);
    }

    public static FcmToken fromEntity(UUID id, UUID userId, String value, LocalDateTime createdAt) {
        return new FcmToken(id, createdAt, userId, value);
    }

    public FcmToken changeUserId(UUID userId) {
        this.userId = userId;
        return this;
    }
}
