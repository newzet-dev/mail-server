package com.newzet.api.article.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.newzet.api.article.repository.dto.ArticleWithImageProjection;
import com.newzet.api.article.repository.entity.ArticleEntity;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface ArticleJpaRepository extends JpaRepository<ArticleEntity, UUID> {

	@Query(value = """
		SELECT
			a.id,
			a.from_name,
			a.title,
			a.is_read,
			a.created_at,
			a.image_url
		FROM article a
		WHERE a.to_user_id = :userId
		AND a.created_at >= make_date(:year, :month, 1)
		AND a.created_at < make_date(:year, :month, 1) + INTERVAL '1 month'
		ORDER BY a.created_at ASC;
		""", nativeQuery = true)
	List<ArticleWithImageProjection> findMonthlyArticlesWithImage(
		@Param("userId") UUID userId,
		@Param("year") int year,
		@Param("month") int month
	);

	@Query(value = """
		SELECT 
		    a.id AS id,
		    a.from_name,
		    a.title,
		    a.is_read,
		    a.created_at,
		    a.image_url
		FROM article a
		WHERE a.to_user_id = :userId and a.is_like = true
		ORDER BY a.created_at DESC;
		""", nativeQuery = true)
	List<ArticleWithImageProjection> findLikeArticleWithImage(@Param("userId") UUID userId);


}
