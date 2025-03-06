package com.newzet.api.newsletter.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.newsletter.business.NewsletterRepository;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsletterRepositoryImpl implements NewsletterRepository {

	private final NewsletterJpaRepository newsletterJpaRepository;

	@Override
	@Transactional
	public NewsletterEntityDto save(String name, String domain, String mailingList, String status) {
		NewsletterEntity newsletterEntity = NewsletterEntity.create(name, domain, mailingList,
			status);
		NewsletterEntity savedNewsletterEntity = newsletterJpaRepository.save(newsletterEntity);
		return savedNewsletterEntity.toEntityDto();
	}

	@Override
	public Optional<NewsletterEntityDto> findByDomainOrMailingList(String domain,
		String mailingList) {
		return Optional.empty();
		// return newsletterJpaRepository.findNewsletterByDomainOrMailingList(domain, mailingList)
		// 	.map(NewsletterEntity::toModel);
	}
}
