package com.newzet.api.article.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.business.repository.ArticleRepository;
import com.newzet.api.article.repository.entity.ArticleEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepository {

	private static final int BATCH_SIZE = 50;
	private final ArticleJpaRepository articleJpaRepository;
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public List<ArticleEntityDto> saveAll(List<ArticleEntityDto> articleEntityDtoList) {
		if (articleEntityDtoList.isEmpty()) {
			return List.of();
		}

		List<ArticleEntity> entities = articleEntityDtoList.stream()
			.map(ArticleEntity::fromEntityDto)
			.toList();

		List<ArticleEntityDto> result = new ArrayList<>();
		int i = 0;

		for (ArticleEntity entity : entities) {
			if (entity.getId() == null) {
				entityManager.persist(entity);
			} else {
				entityManager.merge(entity);
			}

			result.add(entity.toEntityDto());
			i++;

			if (i % BATCH_SIZE == 0) {
				entityManager.flush();
				entityManager.clear();
			}
		}

		if (i % BATCH_SIZE != 0) {
			entityManager.flush();
			entityManager.clear();
		}

		return result;
	}

	@Override
	public boolean existsByFromNameAndFromDomainAndTitleAndToUserIdAndDeletedAtIsNull(
		String fromName, String fromDomain, String title, UUID toUserId) {
		return articleJpaRepository.existsByFromNameAndFromDomainAndTitleAndToUserIdAndDeletedAtIsNull(
			fromName, fromDomain, title, toUserId);
	}
}
