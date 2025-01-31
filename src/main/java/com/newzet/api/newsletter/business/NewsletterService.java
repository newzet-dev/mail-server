package com.newzet.api.newsletter.business;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsletterService {

	private final NewsletterRepository newsletterRepository;

	public Newsletter findOrCreateNewsletter(String name, String domain, String mailingList) {
		Optional<Newsletter> foundNewsletter = newsletterRepository.findByDomainOrMailingList(
			domain, mailingList);
		return foundNewsletter.orElseGet(() -> newsletterRepository.save(name, domain, mailingList,
			NewsletterStatus.UNREGISTERED));
	}
}
