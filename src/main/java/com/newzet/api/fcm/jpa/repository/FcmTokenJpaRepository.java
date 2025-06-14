package com.newzet.api.fcm.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newzet.api.fcm.jpa.entity.FcmTokenEntity;

public interface FcmTokenJpaRepository extends JpaRepository<FcmTokenEntity, UUID> {
	Optional<FcmTokenEntity> findByFcmToken(String value);

	Optional<FcmTokenEntity> findByUserIdAndFcmToken(UUID userId, String fcmToken);
}
