package com.newzet.api.newsletter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newzet.api.subscription.repository.SubscriptionJpaEntity;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionJpaEntity, Long> {
}
