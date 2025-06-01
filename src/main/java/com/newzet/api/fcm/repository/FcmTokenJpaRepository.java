package com.newzet.api.fcm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FcmTokenJpaRepository extends JpaRepository<FcmTokenEntity, UUID> {
}
