package com.newzet.api.newsletter.domain;

import org.springframework.stereotype.Component;

import com.newzet.api.newsletter.repository.NewsletterRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NewsletterManager {

	private final NewsletterRepository newsletterRepository;

	public Newsletter findOrCreateNewsletter(String name, String domain, String maillingList) {
		// TODO: 조회하여 있으면 리턴, 없으면 생성
		return newsletterRepository.findNewsletter(domain, maillingList);
	}
}
