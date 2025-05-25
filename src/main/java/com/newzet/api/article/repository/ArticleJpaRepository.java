package com.newzet.api.article.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newzet.api.article.repository.entity.ArticleEntity;

@Repository
public interface ArticleJpaRepository extends JpaRepository<ArticleEntity, UUID> {
	boolean existsByFromNameAndFromDomainAndTitleAndToUserIdAndDeletedAtIsNull(
		String fromName, String fromDomain, String title, UUID toUserId);
}
