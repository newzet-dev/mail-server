package com.newzet.api.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newzet.api.user.domain.UserStatus;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
	Optional<UserJpaEntity> findByEmailAndStatus(String email, UserStatus status);
}
