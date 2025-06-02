package com.newzet.api.article.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.business.repository.ArticleRepository;
import com.newzet.api.article.repository.entity.ArticleEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ArticleRepositoryImpl implements ArticleRepository {

	private static final int BATCH_SIZE = 100;
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public List<ArticleEntityDto> saveAll(List<ArticleEntityDto> articleEntityDtoList) {
		if (articleEntityDtoList.isEmpty()) {
			return List.of();
		}

		List<ArticleEntity> entitiesToSave = articleEntityDtoList.stream()
			.map(ArticleEntity::fromEntityDto)
			.toList();

		List<ArticleEntityDto> result = new ArrayList<>();
		int processedArticles = 0;

		for (ArticleEntity entityToSave : entitiesToSave) {
			try {
				entityManager.persist(entityToSave);
			} catch (Exception e) {
				log.error("Failed to persist ArticleEntity: {}", entityToSave, e);
			}
			result.add(entityToSave.toEntityDto());
			processedArticles++;

			if (processedArticles % BATCH_SIZE == 0) {
				entityManager.flush();
				entityManager.clear();
			}
		}

		if (processedArticles % BATCH_SIZE != 0) {
			entityManager.flush();
			entityManager.clear();
		}

		return result;
	}
}
