package com.newzet.api.welcome.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newzet.api.welcome.jpa.entity.UserinfoEntity;

interface UserinfoJpaRepository extends JpaRepository<UserinfoEntity, UUID> {
	Optional<UserinfoEntity> findOptionalByEmail(String email);
}
