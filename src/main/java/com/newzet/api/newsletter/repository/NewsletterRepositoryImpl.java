package com.newzet.api.newsletter.repository;

import org.springframework.stereotype.Component;

import com.newzet.api.newsletter.domain.Newsletter;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NewsletterRepositoryImpl implements NewsletterRepository {

	private final NewsletterJpaRepository newsletterJpaRepository;

	@Override
	public Newsletter findNewsletter(String domain, String maillingList) {
		NewsletterJpaEntity newsletterJpaEntity = newsletterJpaRepository
			.findNewsletterByDomainOrMaillingList(domain, maillingList)
			.orElse(null);
		return newsletterJpaEntity == null ? null : newsletterJpaEntity.toNewsletter();
	}
}
