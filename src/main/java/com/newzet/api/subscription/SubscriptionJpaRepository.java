package com.newzet.api.subscription;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionJpaEntity, Long> {
	List<SubscriptionJpaEntity> findAllByUserId(Long userId);
}
