package com.newzet.api.fcm.repository;

import com.newzet.api.fcm.business.dto.FcmTokenEntityResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "fcm_tokens")
@Getter
public class FcmTokenEntity {
    @Id
    @UuidGenerator
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String fcmToken;

    private LocalDateTime createdAt;

    public static FcmTokenEntity create(UUID userId, String value) {
        return FcmTokenEntity.builder().userId(userId).fcmToken(value)
                .createdAt(LocalDateTime.now()).build();
    }

    public FcmTokenEntityResponse toResponseDto() {
        return new FcmTokenEntityResponse(id, userId, fcmToken, createdAt);
    }
}
