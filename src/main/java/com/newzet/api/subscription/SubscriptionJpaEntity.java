package com.newzet.api.subscription;

import com.newzet.api.newsletter.NewsletterJpaEntity;
import com.newzet.api.user.UserJpaEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SubscriptionJpaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserJpaEntity user;

	@ManyToOne
	@JoinColumn(name = "newsletter_id")
	private NewsletterJpaEntity newsletter;
}
