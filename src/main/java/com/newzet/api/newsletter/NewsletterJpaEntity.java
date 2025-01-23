package com.newzet.api.newsletter;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsletterJpaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private NewsletterStatus status;

	public Newsletter toNewsletter() {
		if (status == NewsletterStatus.REGISTERED) {
			return new RegisteredNewsletter();
		}
		return new UnregisteredNewsletter();
	}
}
