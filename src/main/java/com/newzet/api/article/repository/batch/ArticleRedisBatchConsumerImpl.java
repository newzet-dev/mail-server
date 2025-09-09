package com.newzet.api.article.repository.batch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.newzet.api.article.business.batch.ArticleBatchConsumer;
import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.business.repository.ArticleRepository;
import com.newzet.api.article.domain.Article;
import com.newzet.api.article.repository.batch.dto.BatchSaveData;
import com.newzet.api.common.batch.RedisBatchConsumer;
import com.newzet.api.common.batch.config.BatchConfig;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;
import com.newzet.api.fcm.orchestrator.FcmSenderOrchestrator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ArticleRedisBatchConsumerImpl extends RedisBatchConsumer<Article>
	implements ArticleBatchConsumer {

	private static final String ARTICLE_STREAM_KEY = "article:stream";
	private static final String CONSUMER_GROUP = "article-processor-group";
	private static final String CONSUMER_NAME = "article-processor";
	private static final String ARTICLE_DUPLICATE_CACHE_PREFIX = "article:dup:";
	private static final long DUPLICATE_CACHE_TTL_MINUTES = 10;
	private static final int REDIS_BATCH_SIZE = 50;

	private final ArticleRepository articleRepository;
	private final FcmSenderOrchestrator fcmSenderOrchestrator;

	public ArticleRedisBatchConsumerImpl(RedisTemplate<String, String> redisTemplate,
		ReactiveRedisTemplate<String, String> reactiveRedisTemplate,
		BatchConfig batchConfig,
		OptionalObjectMapper optionalObjectMapper,
		ArticleRepository articleRepository,
		FcmSenderOrchestrator fcmSenderOrchestrator) {
		super(redisTemplate, reactiveRedisTemplate, batchConfig, optionalObjectMapper);
		this.articleRepository = articleRepository;
		this.fcmSenderOrchestrator = fcmSenderOrchestrator;
	}

	@Override
	protected String getStreamKey() {
		return ARTICLE_STREAM_KEY;
	}

	@Override
	protected String getConsumerGroup() {
		return CONSUMER_GROUP;
	}

	@Override
	protected String getConsumerName() {
		return CONSUMER_NAME;
	}

	@Override
	protected String getProcessorTypeName() {
		return "Article";
	}

	@Override
	protected Class<Article> getItemClass() {
		return Article.class;
	}

	@Override
	protected void processBatchItems(List<Article> articles) {
		long startTime = System.currentTimeMillis();

		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger duplicateCount = new AtomicInteger(0);
		AtomicInteger failCount = new AtomicInteger(0);
		AtomicInteger cacheHitCount = new AtomicInteger(0);

		Map<String, ArticleEntityDto> uniqueArticlesMap = removeBatchDuplicates(articles,
			duplicateCount, failCount);

		if (uniqueArticlesMap.isEmpty()) {
			log.info("No articles to process after batch deduplication");
			return;
		}

		BatchSaveData saveData = identifyUniqueArticlesWithBatch(uniqueArticlesMap,
			duplicateCount, cacheHitCount, failCount);

		List<ArticleEntityDto> savedArticles = saveToDatabaseOptimized(saveData.toSave(),
			successCount, failCount);

		updateRedisCacheAsync(saveData.toCache());

		sendFCMAsync(savedArticles);

		long duration = System.currentTimeMillis() - startTime;
		logBatchSummary(articles.size(), successCount.get(), duplicateCount.get(),
			cacheHitCount.get(), failCount.get(), duration);
	}

	private Map<String, ArticleEntityDto> removeBatchDuplicates(List<Article> articles,
		AtomicInteger duplicateCount, AtomicInteger failCount) {

		Map<String, ArticleEntityDto> uniqueArticlesMap = new HashMap<>();

		for (Article article : articles) {
			try {
				ArticleEntityDto entityDto = ArticleEntityDto.fromDomain(article);
				String cacheKey = generateOptimizedCacheKey(entityDto);

				if (uniqueArticlesMap.containsKey(cacheKey)) {
					duplicateCount.incrementAndGet();
				} else {
					uniqueArticlesMap.put(cacheKey, entityDto);
				}

			} catch (Exception e) {
				failCount.incrementAndGet();
				log.error("Failed to process article: {}", article.getTitle(), e);
			}
		}

		return uniqueArticlesMap;
	}

	private BatchSaveData identifyUniqueArticlesWithBatch(
		Map<String, ArticleEntityDto> uniqueArticlesMap,
		AtomicInteger duplicateCount, AtomicInteger cacheHitCount, AtomicInteger failCount) {

		List<ArticleEntityDto> toSave = new ArrayList<>();
		Map<String, String> toCache = new HashMap<>();

		List<String> cacheKeys = new ArrayList<>(uniqueArticlesMap.keySet());

		for (int i = 0; i < cacheKeys.size(); i += REDIS_BATCH_SIZE) {
			int endIndex = Math.min(i + REDIS_BATCH_SIZE, cacheKeys.size());
			List<String> batchKeys = cacheKeys.subList(i, endIndex);

			try {
				List<String> cachedValues = redisTemplate.opsForValue().multiGet(batchKeys);

				for (int j = 0; j < batchKeys.size(); j++) {
					String cacheKey = batchKeys.get(j);
					String cachedValue = cachedValues.get(j);
					ArticleEntityDto entityDto = uniqueArticlesMap.get(cacheKey);

					if (cachedValue != null) {
						duplicateCount.incrementAndGet();
						cacheHitCount.incrementAndGet();
					} else {
						toSave.add(entityDto);
						toCache.put(cacheKey, "1");
					}
				}
			} catch (Exception e) {
				log.error("Redis batch query failed: {}", e.getMessage());
				failCount.addAndGet(batchKeys.size());
				// Redis 실패 시 모든 아티클을 저장 대상으로 (안전장치)
				for (String key : batchKeys) {
					toSave.add(uniqueArticlesMap.get(key));
					toCache.put(key, "1");
				}
			}
		}

		return new BatchSaveData(toSave, toCache);
	}

	private List<ArticleEntityDto> saveToDatabaseOptimized(List<ArticleEntityDto> toSave,
		AtomicInteger successCount, AtomicInteger failCount) {

		if (toSave.isEmpty()) {
			return List.of();
		}

		List<ArticleEntityDto> saved = articleRepository.saveAll(toSave);
		successCount.addAndGet(saved.size());
		log.info("Successfully saved {} articles to database", saved.size());
		return saved;
	}

	private void updateRedisCacheAsync(Map<String, String> toCache) {
		if (toCache.isEmpty()) {
			return;
		}

		reactiveRedisTemplate.opsForValue()
			.multiSet(toCache)
			.doOnSuccess(result -> {
				if (Boolean.TRUE.equals(result)) {
					log.debug("Cache updated for {} keys", toCache.size());
				}
			})
			.doOnError(error -> log.error("Cache update failed: {}", error.getMessage()))
			.subscribe();

		for (String key : toCache.keySet()) {
			reactiveRedisTemplate.expire(key, Duration.ofMinutes(DUPLICATE_CACHE_TTL_MINUTES))
				.doOnError(
					error -> log.warn("TTL setting failed for key {}: {}", key, error.getMessage()))
				.subscribe();
		}
	}

	private void sendFCMAsync(List<ArticleEntityDto> savedArticles) {
		if (savedArticles.isEmpty()) {
			return;
		}

		for (ArticleEntityDto articleData : savedArticles) {
			try {
				fcmSenderOrchestrator.sendFcmWhenMailReceivedBatch(
					articleData.getToUserId(),
					articleData.getId(),
					articleData.getCreatedAt(),
					articleData.getTitle(),
					articleData.getFromName()
				);
			} catch (Exception e) {
				log.error("FCM send failed for article {}: {}", articleData.getId(),
					e.getMessage());
			}
		}
	}

	private String generateOptimizedCacheKey(ArticleEntityDto entityDto) {
		String fromName = normalizeString(entityDto.getFromName());
		String fromDomain = normalizeString(entityDto.getFromDomain());
		String title = normalizeString(entityDto.getTitle());
		String userId = entityDto.getToUserId().toString().substring(0, 8);

		return ARTICLE_DUPLICATE_CACHE_PREFIX +
			fromName + ":" +
			fromDomain + ":" +
			userId + ":" +
			Math.abs(title.hashCode());
	}

	private String normalizeString(String input) {
		return input != null ? input.trim().toLowerCase() : "";
	}

	private void logBatchSummary(int totalArticles, int successCount, int duplicateCount,
		int cacheHitCount, int failCount, long duration) {

		log.info(
			"ARTICLE BATCH: total={}, success={}, duplicate={}, cacheHit={}, failed={}, elapsed={}ms",
			totalArticles, successCount, duplicateCount, cacheHitCount, failCount, duration);
	}
}
