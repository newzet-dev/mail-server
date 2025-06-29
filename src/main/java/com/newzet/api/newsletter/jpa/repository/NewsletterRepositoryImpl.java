package com.newzet.api.newsletter.jpa.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.newsletter.business.repository.NewsletterRepository;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.exception.NoNewsletterException;
import com.newzet.api.newsletter.jpa.entity.NewsletterEntity;
import com.newzet.api.newsletter.jpa.mapper.NewsletterEntityMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsletterRepositoryImpl implements NewsletterRepository {

	private final NewsletterJpaRepository jpaRepository;

	@Override
	public Newsletter save(Newsletter newsletter) {
		NewsletterEntity savedNewsletter = jpaRepository.save(NewsletterEntityMapper.toEntity(newsletter));
		return NewsletterEntityMapper.toDomain(savedNewsletter);
	}

	@Override
	public List<Newsletter> findNewsLetterListByName(String name) {
		return jpaRepository.findNewsletterListByName(name).stream()
			.map(NewsletterEntityMapper::toDomain)
			.toList();
	}

	@Override
	public List<Newsletter> findNewsLetterListByCategoryId(UUID categoryId) {
		return jpaRepository.findNewsletterListByCategoryId(categoryId).stream()
			.map(NewsletterEntityMapper::toDomain)
			.toList();
	}

	@Override
	public Newsletter findById(UUID id) {
		return jpaRepository.findById(id)
			.map(NewsletterEntityMapper::toDomain)
			.orElseThrow(() -> new NoNewsletterException("해당 id의 뉴스레터가 존재하지 않습니다."));
	}

	@Override
	public List<Newsletter> findNewsletterListByCategoryIdList(List<UUID> categoryIdList) {
		return jpaRepository.findByCategoryIdList(categoryIdList).stream()
			.map(NewsletterEntityMapper::toDomain)
			.toList();
	}

	@Override
	public List<Newsletter> findNewsletterListByIdList(List<UUID> idList) {
		return jpaRepository.findAllById(idList).stream()
			.map(NewsletterEntityMapper::toDomain)
			.toList();
	}
}
