package com.newzet.api.subscription.repository.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.newzet.api.subscription.repository.entity.SubscriptionEntity;
import com.newzet.api.subscription.repository.repository.dto.SubscriptionListWithImageProjection;

import io.lettuce.core.dynamic.annotation.Param;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, UUID> {
	@Query(value = """
			SELECT
			  fs.id,
			  fs.newsletter_name,
			  n.domain,
			  n.image_url,
			  n.status,
			  n.day_of_week
			FROM
			  (SELECT *
			  FROM subscriptions s
			  WHERE s.user_id = :userId
			  ) fs
			LEFT OUTER JOIN
			  newsletters n
			ON
			  fs.newsletter_domain = n.domain
		""", nativeQuery = true)
	List<SubscriptionListWithImageProjection> getSubscriptionWithImage(@Param("userId") UUID userId);
}

