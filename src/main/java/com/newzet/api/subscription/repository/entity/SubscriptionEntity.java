package com.newzet.api.subscription.repository.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.newzet.api.newsletter.repository.NewsletterEntity;
import com.newzet.api.user.repository.entity.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SUBSCRIPTIONS")
public class SubscriptionEntity {
	@Id
	@Column(columnDefinition = "UUID DEFAULT gen_random_uuid()", updatable = false, nullable = false)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne
	@JoinColumn(name = "newsletter_id")
	private NewsletterEntity newsletter;

	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT NOW()", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private LocalDateTime deletedAt;

	public static SubscriptionEntity create(UserEntity user, NewsletterEntity newsletter) {
		return SubscriptionEntity.builder()
			.user(user)
			.newsletter(newsletter)
			.build();
	}
}
