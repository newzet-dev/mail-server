package com.newzet.api.article.repository;

import static com.newzet.api.article.repository.entity.QArticleEntity.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.article.business.repository.ArticleQueryRepository;
import com.newzet.api.article.repository.dto.ArticleWithImageProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArticleJpaQueryRepository implements ArticleQueryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<ArticleWithImageProjection> findMonthlyArticlesWithImage(UUID userId, int year,
		int month) {

		LocalDate startDate = LocalDate.of(year, month, 1);
		LocalDate endDate = startDate.plusMonths(1); // 다음 달 1일

		return queryFactory
			.select(Projections.constructor(ArticleWithImageProjection.class,
				articleEntity.id,
				articleEntity.fromName,
				articleEntity.title,
				articleEntity.isRead,
				articleEntity.createdAt,
				articleEntity.imageUrl
			))
			.from(articleEntity)
			.where(articleEntity.toUserId.eq(userId)
				.and(articleEntity.createdAt.goe(startDate.atStartOfDay()))
				.and(articleEntity.createdAt.lt(endDate.atStartOfDay())))
			.orderBy(articleEntity.createdAt.asc())
			.fetch();
	}

	@Override
	public List<ArticleWithImageProjection> findLikeArticleWithImage(UUID userId) {
		return queryFactory
			.select(Projections.constructor(ArticleWithImageProjection.class,
				articleEntity.id,
				articleEntity.fromName,
				articleEntity.title,
				articleEntity.isRead,
				articleEntity.createdAt,
				articleEntity.imageUrl
			))
			.from(articleEntity)
			.where(
				articleEntity.toUserId.eq(userId),
				articleEntity.isLike.isTrue()
			)
			.orderBy(articleEntity.createdAt.desc())
			.fetch();
	}
}
