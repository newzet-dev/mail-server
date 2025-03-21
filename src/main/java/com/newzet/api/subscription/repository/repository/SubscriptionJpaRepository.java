package com.newzet.api.subscription.repository.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newzet.api.subscription.repository.entity.SubscriptionEntity;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, UUID> {
	Optional<SubscriptionEntity> findByUserIdAndNewsletterId(Long userId, Long newsletterId);
}
