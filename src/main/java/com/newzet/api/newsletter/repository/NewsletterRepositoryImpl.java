package com.newzet.api.newsletter.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.newsletter.business.NewsletterRepository;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.repository.exception.NoNewsletterException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsletterRepositoryImpl implements NewsletterRepository {

	private final NewsletterJpaRepository newsletterJpaRepository;

	@Override
	public NewsletterEntityDto save(String name, String domain, String mailingList, String status) {
		NewsletterEntity newsletterEntity = NewsletterEntity.create(name, domain, mailingList,
			status);
		return newsletterJpaRepository.save(newsletterEntity).toEntityDto();
	}

	@Override
	public Optional<NewsletterEntityDto> findByDomainOrMailingList(String domain,
		String mailingList) {
		return newsletterJpaRepository.findNewsletterByDomainOrMailingList(domain, mailingList)
			.map(NewsletterEntity::toEntityDto);
	}

	@Override
	public List<NewsletterEntityDto> findNewsLetterListByName(String name) {
		return newsletterJpaRepository.findNewsletterListByName(name).stream()
			.map(NewsletterEntity::toEntityDto)
			.toList();
	}

	@Override
	public List<NewsletterEntityDto> findNewsLetterListByCategoryId(UUID categoryId) {
		return newsletterJpaRepository.findNewsletterListByCategoryId(categoryId).stream()
			.map(NewsletterEntity::toEntityDto)
			.toList();
	}

	@Override
	public NewsletterEntityDto getById(UUID id) {
		return newsletterJpaRepository.findById(id)
			.orElseThrow(() -> new NoNewsletterException("해당 id의 뉴스레터가 존재하지 않습니다."))
			.toEntityDto();
	}

	@Override
	public List<NewsletterEntityDto> getNewsLetterListByCategoryIdList(List<UUID> categoryIdList) {
		return newsletterJpaRepository.findByCategoryIdList(categoryIdList).stream()
			.map(NewsletterEntity::toEntityDto)
			.toList();
	}
}
