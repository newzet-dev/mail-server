package com.newzet.api.fcm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FcmTokenJpaRepository extends JpaRepository<FcmTokenEntity, UUID> {
    Optional<FcmTokenEntity> findByFcmToken(String value);

    Optional<FcmTokenEntity> findByUserIdAndFcmToken(UUID userId, String fcmToken);
}
