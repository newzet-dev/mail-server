package com.newzet.api.newsletter.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.newsletter.business.NewsletterRepository;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.NewsletterStatus;
import com.newzet.api.newsletter.dto.NewsletterReadDbDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsletterRepositoryImpl implements NewsletterRepository {

	private final NewsletterJpaRepository newsletterJpaRepository;

	@Override
	@Transactional
	public Newsletter save(String name, String domain, String maillingList,
		NewsletterStatus status) {
		NewsletterEntity newsletterEntity = NewsletterEntity.builder()
			.name(name)
			.domain(domain)
			.maillingList(maillingList)
			.status(NewsletterEntityStatus.valueOf(status.name()))
			.build();
		return newsletterJpaRepository.save(newsletterEntity).toModel();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<NewsletterReadDbDto> findByDomainOrMaillingList(String domain,
		String maillingList) {
		return newsletterJpaRepository.findNewsletterByDomainOrMaillingList(domain, maillingList)
			.map(NewsletterEntity::toReadDto);
	}
}
