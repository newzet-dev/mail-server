package com.newzet.api.fcm.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenJpaRepository extends JpaRepository<FcmTokenEntity, UUID> {
	Optional<FcmTokenEntity> findByUserIdAndFcmToken(UUID userId, String fcmTOken);
}
