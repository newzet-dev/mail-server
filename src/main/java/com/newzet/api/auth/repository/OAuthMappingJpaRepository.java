package com.newzet.api.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newzet.api.auth.domain.OAuthProvider;
import com.newzet.api.auth.repository.entity.OAuthMappingEntity;

@Repository
public interface OAuthMappingJpaRepository extends JpaRepository<OAuthMappingEntity, UUID> {

	Optional<OAuthMappingEntity> findBySocialUserIdAndProvider(String socialUserId,
		OAuthProvider provider);

	Optional<OAuthMappingEntity> findByIdAndProvider(UUID id, OAuthProvider provider);
}
