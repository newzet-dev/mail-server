package com.newzet.api.newsletter.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.newsletter.business.NewsletterRepository;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsletterRepositoryImpl implements NewsletterRepository {

	private final NewsletterJpaRepository newsletterJpaRepository;

	@Override
	@Transactional
	public Newsletter save(String name, String domain, String maillingList,
		NewsletterStatus status) {
		NewsletterEntity newsletterEntity = NewsletterEntity.create(name, domain, maillingList,
			NewsletterEntityStatus.valueOf(status.name()));
		return newsletterJpaRepository.save(newsletterEntity).toModel();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Newsletter> findByDomainOrMaillingList(String domain,
		String maillingList) {
		return newsletterJpaRepository.findNewsletterByDomainOrMaillingList(domain, maillingList)
			.map(NewsletterEntity::toModel);
	}
}
