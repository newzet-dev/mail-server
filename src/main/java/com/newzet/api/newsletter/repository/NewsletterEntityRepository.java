package com.newzet.api.newsletter.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NewsletterEntityRepository {
	private final NewsletterJpaRepository newsletterJpaRepository;

	public List<NewsletterJpaEntity> findNewsletterListByUserId(Long userId) {
		return newsletterJpaRepository.findAllByUserId(userId);
	}
}
