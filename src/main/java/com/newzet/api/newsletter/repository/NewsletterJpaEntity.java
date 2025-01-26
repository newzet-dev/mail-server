package com.newzet.api.newsletter.repository;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;
import com.newzet.api.newsletter.domain.RegisteredNewsletter;
import com.newzet.api.newsletter.domain.UnregisteredNewsletter;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsletterJpaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private NewsletterStatus status;

	public NewsletterJpaEntity(NewsletterStatus status) {
		this.status = status;
	}

	public Newsletter toNewsletter() {
		if (this.status == NewsletterStatus.REGISTERED) {
			return new RegisteredNewsletter();
		}
		return new UnregisteredNewsletter();
	}
}
