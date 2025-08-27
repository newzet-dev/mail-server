package com.newzet.api.article.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.newzet.api.article.repository.entity.ArticleEntity;
import com.newzet.api.article.repository.entity.ArticleEntity.ArticleEntityBuilder;
import com.newzet.api.category.jpa.CategoryEntity;
import com.newzet.api.category.jpa.CategoryJpaRepository;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.config.RedisTestContainerConfig;
import com.newzet.api.newsletter.domain.NewsletterColor;
import com.newzet.api.newsletter.jpa.entity.NewsletterEntity;
import com.newzet.api.newsletter.jpa.repository.NewsletterJpaRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ExtendWith({PostgresTestContainerConfig.class, RedisTestContainerConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArticleImageUrlUpdateTest {

	private final LocalDateTime BATCH_START_DATE = LocalDateTime.of(2024, 1, 1, 0, 0);
	private final LocalDateTime BATCH_END_DATE = LocalDateTime.of(2024, 2, 1, 0, 0);
	private final String NEWSLETTER_IMG_URL_1 = "https://cdn.images.com/newsletter/1.jpg";
	private final String NEWSLETTER_IMG_URL_2 = "https://cdn.images.com/newsletter/2.jpg";
	private final String NEWSLETTER_IMG_URL_3 = "https://cdn.images.com/newsletter/3.jpg";
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private ArticleJpaRepository articleJpaRepository;
	@Autowired
	private NewsletterJpaRepository newsletterJpaRepository;
	@Autowired
	private CategoryJpaRepository categoryJpaRepository;

	private ArticleEntity successCaseByDomain;
	private ArticleEntity successCaseByMailingList;
	private ArticleEntity ignoredCaseAlreadyHasImage;
	private ArticleEntity ignoredCaseNoMatch;
	private ArticleEntity ignoredCaseOutOfDateRange;

	@BeforeEach
	void setUp() {
		// Given
		// === 1. 기본 카테고리 생성 ===
		CategoryEntity categoryEntity = categoryJpaRepository.save(
			new CategoryEntity(null, "category", "imageUrl", "emoji"));

		// === 2. 랜덤 데이터 생성을 위한 준비 ===
		List<NewsletterEntity> newsletters = new ArrayList<>();
		Random random = new Random();
		final int NEWSLETTER_COUNT = 50;
		final int ARTICLE_COUNT = 20000;

		// === 3. 뉴스레터 50개 생성 (최종 수정된 로직) ===
		for (int i = 0; i < NEWSLETTER_COUNT; i++) {
			// [규칙 1] domain은 항상 고유한 값을 가짐
			String domain = "domain-" + i + ".unique-service.com";

			// [규칙 2] mailingList는 간혹 동일한 값을 가짐 (같은 뉴스레터 사를 식별하는 핵심 키)
			// 50개 뉴스레터에 대해 메일링 리스트는 40개 중에서 돌려 사용 -> 10개의 mailingList 중복 발생
			String mailingList = "shared-list-" + (i % (NEWSLETTER_COUNT - 10)) + "@company.com";

			String imageUrl = "https://cdn.images.com/newsletter/" + i + ".jpg";

			newsletters.add(
				new NewsletterEntity(null, "뉴스레터 " + i, categoryEntity.getId(), domain,
					mailingList, i, imageUrl, "", "", "", "", "", NewsletterColor.DEFAULT, null)
			);
		}
		newsletterJpaRepository.saveAll(newsletters);

		// === 4. 아티클 2만개 생성 (랜덤 관계 설정) ===
		List<ArticleEntity> articles = new ArrayList<>();

		// [고정 케이스] 테스트 결과 검증을 위해, 의도된 시나리오의 데이터는 별도로 생성
		// 이 데이터들은 랜덤 로직과 별개로 항상 존재하여 특정 케이스의 성공/실패를 보장합니다.
		NewsletterEntity fixedNewsletter1 = newsletters.get(1); // domain 매칭용
		NewsletterEntity fixedNewsletter2 = newsletters.get(2); // mailingList 매칭용

		// 성공 케이스 1: domain으로 매칭
		successCaseByDomain = ArticleEntity.builder()
			.fromDomain(fixedNewsletter1.getDomain())
			.imageUrl(null)
			.createdAt(BATCH_START_DATE.plusDays(1))
			.build();
		articles.add(successCaseByDomain);

		// 성공 케이스 2: mailingList로 매칭
		successCaseByMailingList = ArticleEntity.builder()
			.fromDomain("some-other-domain.com") // 도메인은 일부러 다르게
			.mailingList(fixedNewsletter2.getMailingList())
			.imageUrl(null)
			.createdAt(BATCH_START_DATE.plusDays(2))
			.build();
		articles.add(successCaseByMailingList);

		// 무시 케이스 1: 이미지가 존재
		ignoredCaseAlreadyHasImage = ArticleEntity.builder()
			.fromDomain(fixedNewsletter1.getDomain())
			.imageUrl("https://my-own-image.com/image.png")
			.createdAt(BATCH_START_DATE.plusDays(3))
			.build();
		articles.add(ignoredCaseAlreadyHasImage);

		// 무시 케이스 2: 매칭 조건 없음
		ignoredCaseNoMatch = ArticleEntity.builder()
			.fromDomain("no-match-domain.com")
			.mailingList("no-match-list@email.com")
			.imageUrl(null)
			.createdAt(BATCH_START_DATE.plusDays(4))
			.build();
		articles.add(ignoredCaseNoMatch);

		// 무시 케이스 3: 날짜 범위 밖
		ignoredCaseOutOfDateRange = ArticleEntity.builder()
			.fromDomain(fixedNewsletter1.getDomain())
			.imageUrl(null)
			.createdAt(BATCH_END_DATE.plusDays(1))
			.build();
		articles.add(ignoredCaseOutOfDateRange);

		// [랜덤 케이스] 나머지 대량의 더미 데이터 생성
		for (int i = 0; i < ARTICLE_COUNT; i++) {
			ArticleEntityBuilder articleBuilder = ArticleEntity.builder()
				.imageUrl(null)
				.createdAt(BATCH_START_DATE.plusMinutes(i));

			int choice = random.nextInt(10); // 0~9 사이의 난수 생성
			NewsletterEntity randomNewsletter = newsletters.get(random.nextInt(NEWSLETTER_COUNT));

			switch (choice) {
				case 0, 1, 2, 3: // 40% 확률: domain으로 매칭
					articleBuilder.fromDomain(randomNewsletter.getDomain());
					break;
				case 4, 5, 6, 7: // 40% 확률: mailingList로 매칭
					// 단, mailingList가 비어있는 뉴스레터가 선택될 수 있으므로 현실적인 케이스 포함
					articleBuilder.fromDomain("random-domain-" + UUID.randomUUID()) // 도메인은 일부러 다르게 함
						.mailingList(randomNewsletter.getMailingList());
					break;
				default: // 20% 확률: 관계가 없는 데이터(업데이트 대상에 포함되지 않음)
					articleBuilder.fromDomain("no-relation-domain-" + UUID.randomUUID())
						.mailingList("no-relation-list-" + UUID.randomUUID());
					break;
			}
			articles.add(articleBuilder.build());
		}

		articleJpaRepository.saveAll(articles);

		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("Article의 imageUrl을 Newsletter 정보로 배치 업데이트 테스트")
	void shouldUpdateArticleImageUrlFromNewsletterInBatch() {
		// When

		// 1. SELECT 조인으로 의도한대로 데이터가 구성되는지 확인
		String selectSql = """
			    SELECT
			        COUNT(DISTINCT a.id)
			    FROM
			        article a, newsletter n
			    WHERE
			        ((a.from_domain = n.domain) OR (a.mailing_list IS NOT NULL AND a.mailing_list = n.mailing_list))
			        AND a.image_url IS NULL
			        AND a.created_at >= ?1
			        AND a.created_at < ?2
			""";

		long expectedUpdateCount = (long)entityManager.createNativeQuery(selectSql)
			.setParameter(1, BATCH_START_DATE)
			.setParameter(2, BATCH_END_DATE)
			.getSingleResult();
		assertTrue(expectedUpdateCount > 0, "조인 조건에 맞는 업데이트 대상 레코드가 조회되어야 합니다.");

		// 2. 실제 UPDATE 구문 실행
		String sql = """
			    UPDATE
			        article a
			    SET
			        image_url = n.image_url
			    FROM
			        newsletter n
			    WHERE
			        ((a.from_domain = n.domain) OR (a.mailing_list IS NOT NULL AND a.mailing_list = n.mailing_list))
			        AND a.image_url IS NULL
			        AND a.created_at >= :startDate
			        AND a.created_at < :endDate
			""";

		int updatedCount = entityManager.createNativeQuery(sql)
			.setParameter("startDate", BATCH_START_DATE)
			.setParameter("endDate", BATCH_END_DATE)
			.executeUpdate();

		entityManager.flush();
		entityManager.clear();

		// Then
		assertEquals(expectedUpdateCount, updatedCount, "사전 조회된 레코드 수와 실제 업데이트된 레코드 수가 일치해야 합니다.");

		// 1. 성공 케이스(domain) 검증
		ArticleEntity updatedSuccessCase1 = articleJpaRepository.findById(
				successCaseByDomain.getId())
			.orElseThrow();
		assertEquals(NEWSLETTER_IMG_URL_1, updatedSuccessCase1.getImageUrl(),
			"Domain으로 매칭된 Article의 imageUrl이 업데이트되어야 합니다.");

		// 2. 성공 케이스(mailingList) 검증
		ArticleEntity updatedSuccessCase2 = articleJpaRepository.findById(
			successCaseByMailingList.getId()).orElseThrow();
		assertEquals(NEWSLETTER_IMG_URL_2, updatedSuccessCase2.getImageUrl(),
			"MailingList로 매칭된 Article의 imageUrl이 업데이트되어야 합니다.");

		// 3. 무시 케이스(이미지 존재) 검증
		ArticleEntity notUpdatedCase1 = articleJpaRepository.findById(
			ignoredCaseAlreadyHasImage.getId()).orElseThrow();
		assertNotEquals(NEWSLETTER_IMG_URL_1, notUpdatedCase1.getImageUrl(),
			"이미 imageUrl이 있는 Article은 업데이트되면 안 됩니다.");
		assertEquals("https://my-own-image.com/image.png", notUpdatedCase1.getImageUrl());

		// 4. 무시 케이스(매칭 없음) 검증
		ArticleEntity notUpdatedCase2 = articleJpaRepository.findById(ignoredCaseNoMatch.getId())
			.orElseThrow();
		assertNull(notUpdatedCase2.getImageUrl(),
			"매칭되는 Newsletter가 없는 Article은 imageUrl이 null로 남아있어야 합니다.");

		// 5. 무시 케이스(날짜 범위 밖) 검증
		ArticleEntity notUpdatedCase3 = articleJpaRepository.findById(
			ignoredCaseOutOfDateRange.getId()).orElseThrow();
		assertNull(notUpdatedCase3.getImageUrl(), "날짜 범위 밖의 Article은 업데이트되면 안 됩니다.");
	}
}
