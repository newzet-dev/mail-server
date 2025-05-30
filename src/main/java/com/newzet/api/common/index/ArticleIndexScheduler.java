package com.newzet.api.common.index;

import java.time.LocalDate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleIndexScheduler {

	private final JdbcTemplate jdbcTemplate;

	// 매월 마지막 날 04:00 실행
	@Scheduled(cron = "4 0 0 L * *")
	public void recreatePartialIndexInFilteringArticle() {
		jdbcTemplate.execute("DROP INDEX IF EXISTS idx_article_recent_user_createdat");

		String sql = String.format("""
			    CREATE INDEX idx_article_recent_user_createdat
			    ON article(to_user_id, created_at)
			    WHERE created_at >= '%s';
			""", LocalDate.now().minusMonths(1).withDayOfMonth(1)); // ex: 2025-05-01

		jdbcTemplate.execute(sql);
	}
}
