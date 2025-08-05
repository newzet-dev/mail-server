package com.newzet.api.userinfo.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newzet.api.userinfo.jpa.entity.UserinfoEntity;

interface UserinfoJpaRepository extends JpaRepository<UserinfoEntity, UUID> {
	Optional<UserinfoEntity> findOptionalByEmail(String email);
}
