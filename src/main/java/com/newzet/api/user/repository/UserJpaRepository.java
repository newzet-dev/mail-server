package com.newzet.api.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newzet.api.user.domain.UserStatus;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByEmailAndStatus(String email, UserStatus status);
}
