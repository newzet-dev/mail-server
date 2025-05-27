package com.newzet.api.article.repository.batch;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newzet.api.article.business.dto.ArticleDto;
import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.business.repository.ArticleRepository;
import com.newzet.api.common.batch.config.BatchConfig;
import com.newzet.api.config.JwtTestConfig;
import com.newzet.api.config.OAuthTestConfig;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.config.RedisTestContainerConfig;

@ExtendWith({RedisTestContainerConfig.class, PostgresTestContainerConfig.class,
	JwtTestConfig.class, OAuthTestConfig.class})
@SpringBootTest
@ComponentScan(basePackages = {"com.newzet.api.article", "com.newzet.api.common"})
class ArticleRedisBatchProcessorIntegrationTest {

	private static final String ARTICLE_STREAM_KEY = "article:stream:test";
	private static final String CONSUMER_GROUP = "test-group";

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ArticleRepository articleRepository;

	@Mock
	private BatchConfig mockBatchConfig;

	private ArticleRedisBatchProcessorImpl batchProcessor;
	private AutoCloseable closeable;

	@BeforeEach
	void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		cleanupResources();

		lenient().when(mockBatchConfig.getBatchSize()).thenReturn(2);
		lenient().when(mockBatchConfig.getTimeoutSeconds()).thenReturn(1);

		batchProcessor = new ArticleRedisBatchProcessorImpl(
			redisTemplate,
			new ReactiveRedisTemplate<>(
				(ReactiveRedisConnectionFactory)redisConnectionFactory,
				org.springframework.data.redis.serializer.RedisSerializationContext.string()
			),
			articleRepository,
			mockBatchConfig,
			objectMapper
		) {
		};

		initializeStream();
	}

	private void initializeStream() {
		try {
			Map<String, String> dummy = new HashMap<>();
			dummy.put("init", "init");
			redisTemplate.opsForStream().add(ARTICLE_STREAM_KEY, dummy);

			try {
				redisTemplate.opsForStream().createGroup(ARTICLE_STREAM_KEY, CONSUMER_GROUP);
			} catch (Exception ignored) {
			}

			assertThat(redisTemplate.hasKey(ARTICLE_STREAM_KEY)).isTrue();
		} catch (Exception e) {
			fail("Failed to initialize stream: " + e.getMessage());
		}
	}

	@AfterEach
	void tearDown() throws Exception {
		cleanupResources();
		closeable.close();
	}

	private void cleanupResources() {
		try {
			if (batchProcessor != null) {
				batchProcessor.stopProcessing();
			}

			Thread.sleep(1000);

			redisTemplate.delete(ARTICLE_STREAM_KEY);
		} catch (Exception ignored) {
		}
	}

	@Test
	void processBatchItems_WhenDuplicateArticles_ThenSkipDuplicates() {
		UUID userId = UUID.randomUUID();
		ArticleDto article1 = createArticleDto(userId, "Unique Article");
		ArticleDto article2 = createArticleDto(userId, "Duplicate Article");

		String duplicateCacheKey =
			"article:dup:newsletter_example.com_" + userId.toString().substring(0, 8) + "_" +
				Math.abs("Duplicate Article".toLowerCase().hashCode());
		redisTemplate.opsForValue().set(duplicateCacheKey, "1", Duration.ofMinutes(10));

		List<ArticleEntityDto> returnedDtos = List.of(ArticleEntityDto.builder()
			.id(UUID.randomUUID())
			.title("Unique Article")
			.fromName("Newsletter")
			.fromDomain("example.com")
			.toUserId(userId)
			.build());

		when(articleRepository.saveAll(anyList())).thenReturn(returnedDtos);

		batchProcessor.init();
		batchProcessor.startProcessing();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		batchProcessor.addToBatch(article1);
		batchProcessor.addToBatch(article2);

		await()
			.pollInterval(500, TimeUnit.MILLISECONDS)
			.atMost(30, TimeUnit.SECONDS)
			.untilAsserted(() -> {
				verify(articleRepository, timeout(20000).atLeastOnce()).saveAll(
					argThat(articles -> {
						System.out.println("SaveAll called with: " + articles);
						if (articles == null || articles.isEmpty()) {
							return false;
						}
						return articles.size() == 1 &&
							"Unique Article".equals(articles.get(0).getTitle());
					}));
			});
	}

	@Test
	void addToBatch_WhenArticlesAdded_ThenProcessedInBatch() {
		UUID userId = UUID.randomUUID();
		ArticleDto article1 = createArticleDto(userId, "First Article");
		ArticleDto article2 = createArticleDto(userId, "Second Article");

		doAnswer(invocation -> {
			List<ArticleEntityDto> dtos = invocation.getArgument(0);
			assertThat(dtos).hasSizeGreaterThanOrEqualTo(1);
			return null;
		}).when(articleRepository).saveAll(anyList());

		batchProcessor.init();
		batchProcessor.startProcessing();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		batchProcessor.addToBatch(article1);
		batchProcessor.addToBatch(article2);

		await()
			.pollInterval(100, TimeUnit.MILLISECONDS)
			.atMost(5, TimeUnit.SECONDS)
			.untilAsserted(() -> {
				verify(articleRepository, atLeastOnce()).saveAll(anyList());
			});
	}

	@Test
	void init_WhenStreamAlreadyExists_ThenSkipStreamCreation() {
		Map<String, String> dummy = new HashMap<>();
		dummy.put("init", "init");
		redisTemplate.opsForStream().add(ARTICLE_STREAM_KEY, dummy);

		batchProcessor.init();

		assertThat(redisTemplate.hasKey(ARTICLE_STREAM_KEY)).isTrue();
	}

	@Test
	void init_WhenRedisConnectionFails_ThenHandleGracefully() {
		RedisTemplate<String, String> mockRedisTemplate = mock(RedisTemplate.class);
		when(mockRedisTemplate.hasKey(any())).thenThrow(
			new RuntimeException("Redis connection failed"));

		ArticleRedisBatchProcessorImpl processorWithMockRedis = new ArticleRedisBatchProcessorImpl(
			mockRedisTemplate,
			new org.springframework.data.redis.core.ReactiveRedisTemplate<>(
				(ReactiveRedisConnectionFactory)redisConnectionFactory,
				org.springframework.data.redis.serializer.RedisSerializationContext.string()
			),
			articleRepository,
			mockBatchConfig,
			objectMapper
		);

		processorWithMockRedis.init();
	}

	@Test
	void getBatchStatus_WhenPendingCountIsNull_ThenReturnZero() {
		RedisTemplate<String, String> mockRedisTemplate = mock(RedisTemplate.class);

		@SuppressWarnings("unchecked")
		StreamOperations<String, Object, Object> mockStreamOps = mock(StreamOperations.class);
		when(mockRedisTemplate.opsForStream()).thenReturn(mockStreamOps);
		when(mockStreamOps.size(any())).thenReturn(null);

		ArticleRedisBatchProcessorImpl processorWithMockRedis = new ArticleRedisBatchProcessorImpl(
			mockRedisTemplate,
			mock(ReactiveRedisTemplate.class),
			mock(ArticleRepository.class),
			mock(BatchConfig.class),
			new ObjectMapper()
		);

		Map<String, Object> status = processorWithMockRedis.getBatchStatus();

		assertThat(status.get("pendingMessages")).isEqualTo(0L);
	}

	private ArticleDto createArticleDto(UUID userId, String title) {
		return ArticleDto.builder()
			.toUserId(userId)
			.fromName("Newsletter")
			.fromDomain("example.com")
			.mailingList("daily")
			.title(title)
			.contentUrl("https://example.com/" + title.toLowerCase().replace(' ', '-'))
			.isRead(false)
			.isLike(false)
			.isShare(false)
			.build();
	}
}
