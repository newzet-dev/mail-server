package com.newzet.api.article.infra.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.newzet.api.article.infra.ArticleEntity;
import com.newzet.api.article.infra.dto.ArticleWithImageProjection;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface ArticleJpaRepository extends JpaRepository<ArticleEntity, UUID> {

	@Query(value = """
		WITH filtered_article AS (
		    SELECT a.id, a.from_name, a.title, a.is_read, a.created_at, a.mailing_list, a.from_domain
		    FROM article a
		    WHERE a.to_user_id = :userId
		      AND a.created_at >= make_date(:year, :month, 1)
		      AND a.created_at < make_date(:year, :month, 1) + INTERVAL '1 month'
		)
		SELECT 
		    fa.id AS id,
		    fa.from_name AS fromName,
		    fa.title AS title,
		    fa.is_read AS isRead,
		    fa.created_at AS createdAt,
		    n.image_url AS imageUrl
		FROM filtered_article fa
		LEFT JOIN newsletters n
		  ON (fa.from_domain = n.domain) OR (fa.mailing_list IS NOT NULL AND fa.mailing_list = n.mailing_list)
		""", nativeQuery = true)
	List<ArticleWithImageProjection> findMonthlyArticlesWithImage(
		@Param("userId") UUID userId,
		@Param("year") int year,
		@Param("month") int month
	);


}
