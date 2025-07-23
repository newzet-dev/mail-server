package com.newzet.api.article.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.newzet.api.article.repository.dto.ArticleWithImageProjection;
import com.newzet.api.article.repository.entity.ArticleEntity;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.config.RedisTestContainerConfig;
import com.newzet.api.config.db.QuerydslConfig;
import com.newzet.api.newsletter.repository.NewsletterEntity;
import com.newzet.api.newsletter.repository.NewsletterEntityStatus;
import com.newzet.api.newsletter.repository.NewsletterJpaRepository;

@DataJpaTest
@Import({QuerydslConfig.class, ArticleJpaQueryRepository.class})
@ExtendWith({PostgresTestContainerConfig.class, RedisTestContainerConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArticleJpaRepositoryTest {

	@Autowired
	private ArticleJpaRepository articleJpaRepository;
	@Autowired
	private ArticleJpaQueryRepository articleJpaQueryRepository;
	@Autowired
	private NewsletterJpaRepository newsletterJpaRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private ObjectMapper objectMapper = new ObjectMapper();

	private NewsletterEntity newsletterEntity;
	private UUID userId;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
		newsletterEntity = newsletterJpaRepository.save(
			NewsletterEntity.create("name", "domain", "mailing-list",
				NewsletterEntityStatus.REGISTERED.toString()));
	}

	@DisplayName("인덱스를 탄다면 날짜별로 정렬된 형태이므로 추가 정렬 작업을 하지 않고 데이터를 반환한다.(실행계획에 Sort 관련 정보 없어야함)")
	@Test
	void return_asc_data_order_by_date_by_index() throws IOException {
		// Given
		// 인덱스 생성
		jdbcTemplate.execute("DROP INDEX IF EXISTS idx_article_user_createdat");
		jdbcTemplate.execute("""
			   CREATE INDEX idx_article_user_createdat
			   ON article(to_user_id, created_at)
			""");

		int year = 2025;
		int month = 1;
		Random random = new Random();
		for (int i = 0; i < 50; i++) {
			int day = random.nextInt(31) + 1;
			ArticleEntity articleEntity = ArticleEntity.builder()
				.toUserId(userId)
				.fromName("name" + i)
				.title("title" + i)
				.fromDomain(newsletterEntity.getDomain())
				.mailingList(newsletterEntity.getMailingList())
				.isRead(false)
				.createdAt(LocalDate.of(year, month, day).atStartOfDay())
				.build();
			articleJpaRepository.save(articleEntity);
		}

		// When
		List<ArticleWithImageProjection> monthlyArticlesWithImage = articleJpaQueryRepository.findMonthlyArticlesWithImage(
			userId, year, month);

		// Then
		boolean isSorted = IntStream.range(0, monthlyArticlesWithImage.size() - 1)
			.noneMatch(i -> monthlyArticlesWithImage.get(i)
				.createdAt()
				.isAfter(monthlyArticlesWithImage.get(i + 1).createdAt()));

		assertTrue(isSorted, "createdAt 필드는 오름차순으로 정렬되어야 합니다.");

		// 실행계획 추출
		String jsonPlan = getExecutionPlan(userId, year, month);

		// JSON 파싱
		JsonNode planNode = objectMapper.readTree(jsonPlan).get(0).get("Plan");

		// Sort 노드 존재 여부 확인
		boolean hasSort = containsSortNode(planNode);

		// 검증: 인덱스를 탔다면 Sort는 없어야 함
		assertFalse(hasSort, "인덱스를 사용하는 경우 실행계획에 Sort 노드가 없어야 합니다.");
	}

	@DisplayName("인덱스를 타지 않는다면 추가 정렬 작업을 하고 데이터를 반환한다.(실행계획에 Sort 관련 정보 있어야함)")
	@Test
	void return_asc_data_order_by_date_with_sorted_job() throws IOException {
		// Given
		int year = 2025;
		int month = 1;
		Random random = new Random();
		for (int i = 0; i < 50; i++) {
			int day = random.nextInt(31) + 1;
			ArticleEntity articleEntity = ArticleEntity.builder()
				.toUserId(userId)
				.fromName("name" + i)
				.title("title" + i)
				.fromDomain(newsletterEntity.getDomain())
				.mailingList(newsletterEntity.getMailingList())
				.isRead(false)
				.createdAt(LocalDate.of(year, month, day).atStartOfDay())
				.build();
			articleJpaRepository.save(articleEntity);
		}

		// When
		List<ArticleWithImageProjection> monthlyArticlesWithImage = articleJpaQueryRepository.findMonthlyArticlesWithImage(
			userId, year, month);

		// Then
		boolean isSorted = IntStream.range(0, monthlyArticlesWithImage.size() - 1)
			.noneMatch(i -> monthlyArticlesWithImage.get(i)
				.createdAt()
				.isAfter(monthlyArticlesWithImage.get(i + 1).createdAt()));

		assertTrue(isSorted, "createdAt 필드는 오름차순으로 정렬되어야 합니다.");

		// 실행계획 추출
		String jsonPlan = getExecutionPlan(userId, year, month);

		// JSON 파싱
		JsonNode planNode = objectMapper.readTree(jsonPlan).get(0).get("Plan");

		// Sort 노드 존재 여부 확인
		boolean hasSort = containsSortNode(planNode);

		// 검증: 인덱스가 없으므로 Sort는 있어야 함
		assertTrue(hasSort, "인덱스를 사용하는 경우 실행계획에 Sort 노드가 없어야 합니다.");

	}

	private String getExecutionPlan(UUID userId, int year, int month) {
		String sql = """
			    EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON)
			    WITH filtered_article AS (
			        SELECT a.id, a.from_name, a.title, a.is_read, a.created_at, a.mailing_list, a.from_domain
			        FROM article a
			        WHERE a.to_user_id = ?
			          AND a.created_at >= make_date(?, ?, 1)
			          AND a.created_at < make_date(?, ?, 1) + INTERVAL '1 month'
			    )
			    SELECT 
			        fa.id,
			        fa.from_name,
			        fa.title,
			        fa.is_read,
			        fa.created_at,
			        n.image_url
			    FROM filtered_article fa
			    LEFT JOIN newsletters n
			      ON (fa.from_domain = n.domain) OR (fa.mailing_list IS NOT NULL AND fa.mailing_list = n.mailing_list)
			    ORDER BY fa.created_at ASC
			""";

		return jdbcTemplate.queryForObject(sql, String.class, userId, year, month, year, month);
	}

	private boolean containsSortNode(JsonNode node) {
		if (node.has("Node Type") && "Sort".equals(node.get("Node Type").asText())) {
			return true;
		}
		if (node.has("Plans")) {
			for (JsonNode subPlan : node.get("Plans")) {
				if (containsSortNode(subPlan)) {
					return true;
				}
			}
		}
		return false;
	}
}
