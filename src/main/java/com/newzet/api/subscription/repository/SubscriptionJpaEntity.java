package com.newzet.api.subscription.repository;

import com.newzet.api.newsletter.repository.NewsletterEntity;
import com.newzet.api.user.repository.UserJpaEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionJpaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserJpaEntity user;

	@ManyToOne
	@JoinColumn(name = "newsletter_id")
	private NewsletterEntity newsletter;

	public SubscriptionJpaEntity(UserJpaEntity user, NewsletterEntity newsletter) {
		this.user = user;
		this.newsletter = newsletter;
	}
}
