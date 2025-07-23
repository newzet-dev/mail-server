package com.newzet.api.article.business;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.business.repository.ArticleRepository;
import com.newzet.api.article.business.service.ArticleService;
import com.newzet.api.article.controller.dto.ArticleContentResponse;
import com.newzet.api.article.controller.dto.ArticleListResponse;
import com.newzet.api.article.controller.dto.DailyArticleResponse;
import com.newzet.api.article.domain.Article;
import com.newzet.api.article.repository.dto.ArticleWithImageProjection;
import com.newzet.api.article.repository.entity.ArticleEntity;
import com.newzet.api.common.exception.InternalErrorException;
import com.newzet.api.common.s3.S3Service;
import com.newzet.api.common.util.UuidConverter;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

	@Mock
	private ArticleRepository articleRepository;
	@Mock
	private S3Service s3Service;
	private String testBucketName = "test-content-bucket";
	@InjectMocks
	private ArticleService articleService;

	@BeforeEach
	void setUp() {
		// @Value로 주입되는 필드 값을 테스트 환경에서 수동으로 주입
		ReflectionTestUtils.setField(articleService, "contentBucketName", testBucketName);
	}

	@DisplayName("월별 아티클 조회 시 날짜별 오름차순 형태로 리스트가 분리되어 저장된다.")
	@Test
	void getMonthlyArticles_monthly_articles_are_sorted_by_date() {
		// Given
		List<ArticleWithImageProjection> articleListAtYearAndMonth = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < 100; i++) {
			int day = random.nextInt(31) + 1;
			LocalDateTime date = LocalDateTime.of(2025, 1, day, 1, 1);
			ArticleWithImageProjection projection = new ArticleWithImageProjection(
				UUID.randomUUID(), "fromName",
				"title", false, date, "imageUrl");
			articleListAtYearAndMonth.add(projection);
		}

		// Repository에서 이미 정렬된 형태로 데이터가 넘어옴을 가정하므로, 정렬된 형태여야 한다.
		articleListAtYearAndMonth.sort(
			Comparator.comparing(ArticleWithImageProjection::createdAt));

		when(articleRepository.getMonthlyArticleWithImage(any(UUID.class), any(Integer.class),
			any(Integer.class)))
			.thenReturn(articleListAtYearAndMonth);

		// When
		ArticleListResponse monthlyArticleList = articleService.getMonthlyArticleList(
			UUID.randomUUID(), 2025, 1);

		// Then

		// 일별 리스트에 있는 아티클의 day와 리스트 멤버의 day와 모두 일치해야 한다.
		monthlyArticleList.dailyArticleList().forEach(dailyArticle -> {
			int day = dailyArticle.day();
			assertThat(day).isBetween(1, 31);
			dailyArticle.articleList().forEach(article -> {
				assertEquals(day, article.getDay());
			});
		});

		List<Integer> days = monthlyArticleList.dailyArticleList().stream()
			.map(DailyArticleResponse::day)
			.toList();
		assertThat(days).isSorted(); // 각 리스트의 날짜들이 오름차순 정렬된 형태로 저장되어 있어야 한다.

	}

	@DisplayName("아티클을 처음 조회하는 경우에 조회 처리가 된다.")
	@Test
	void read_article_in_first_time() {
		// Given
		Article article = Article.create(UUID.randomUUID(), UUID.randomUUID(), "newsletter name",
			"domain",
			"mail-list", "title", "https://", "https://", false, false, false, LocalDateTime.now(),
			LocalDateTime.now());
		ArticleEntityDto updatedArticle = ArticleEntityDto.builder()
			.title("title")
			.contentUrl("https://")
			.isRead(true)
			.build();
		String articleId = article.getId().toString();
		when(articleRepository.getById(any(UUID.class)))
			.thenReturn(ArticleEntityDto.fromDomain(article));
		when(articleRepository.readArticle(any(UUID.class)))
			.thenReturn(updatedArticle);

		// When
		assertFalse(article.isRead());
		ArticleContentResponse articleResponse = articleService.getArticle(articleId);

		// Then
		assertTrue(articleResponse.isLike());
	}

	@DisplayName("아티클을 처음 조회하는 것이 아닌 경우에 조회 처리를 하지 않는다.")
	@Test
	void read_article_not_in_first_time() {
		// Given
		Article article = Article.create(UUID.randomUUID(), UUID.randomUUID(), "newsletter name",
			"domain",
			"mail-list", "title", "https://", "https://", true, false, false, LocalDateTime.now(),
			LocalDateTime.now());
		String articleId = article.getId().toString();
		when(articleRepository.getById(any(UUID.class)))
			.thenReturn(ArticleEntityDto.fromDomain(article));

		// When
		assertTrue(article.isRead());
		ArticleContentResponse articleResponse = articleService.getArticle(articleId);

		// Then
		verify(articleRepository, never()).readArticle(any(UUID.class));
	}

	@Test
	@DisplayName("S3에서 본문을 가져오는 중 예외 발생 시 InternalErrorException을 던진다")
	void getArticle_whenS3Fails_shouldThrowInternalErrorException() {
		// given
		String articleIdStr = UUID.randomUUID().toString();
		UUID articleId = UuidConverter.convert(articleIdStr);
		String contentUrl = "s3-key-that-causes-error.html";

		// 이미 읽은 상태의 Article를 가정
		ArticleEntity readArticleEntity = ArticleEntity.builder()
			.id(articleId)
			.title("title")
			.contentUrl(contentUrl)
			.isRead(true)
			.build();

		when(articleRepository.getById(articleId))
			.thenReturn(readArticleEntity.toEntityDto());
		when(s3Service.getContentAsString(anyString(), anyString()))
			.thenThrow(new InternalErrorException("아티클을 불러오는 과정에서 에러가 발생하였습니다."));

		// when & then
		assertThrows(InternalErrorException.class, () ->
			articleService.getArticle(articleIdStr));

		// verify
		verify(articleRepository).getById(articleId);
		verify(s3Service).getContentAsString(testBucketName, contentUrl);
	}

}