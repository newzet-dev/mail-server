package com.newzet.api.article.repository.batch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.newzet.api.article.business.batch.ArticleBatchConsumer;
import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.business.repository.ArticleRepository;
import com.newzet.api.article.domain.Article;
import com.newzet.api.article.repository.batch.dto.BatchProcessingResult;
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
		BatchProcessingResult result = new BatchProcessingResult();

		Map<String, List<ArticleEntityDto>> keyToArticlesMap = prepareArticlesWithCacheKeys(
			articles, result);

		if (keyToArticlesMap.isEmpty()) {
			log.info("No articles to process");
			return;
		}

		BatchSaveData saveData = identifyUniqueArticles(keyToArticlesMap, result);

		saveToDatabaseAndUpdateCounters(saveData.toSave(), result);

		updateRedisCache(saveData.toCache());

		long duration = System.currentTimeMillis() - startTime;
		logBatchSummary(articles.size(), result, duration);
	}

	private Map<String, List<ArticleEntityDto>> prepareArticlesWithCacheKeys(
		List<Article> articles, BatchProcessingResult result) {
		Map<String, List<ArticleEntityDto>> keyToArticlesMap = new HashMap<>();

		for (Article article : articles) {
			try {
				ArticleEntityDto entityDto = ArticleEntityDto.fromDomain(article);

				String cacheKey = generateSimpleCacheKey(
					entityDto.getFromName(),
					entityDto.getFromDomain(),
					entityDto.getTitle(),
					entityDto.getToUserId()
				);

				keyToArticlesMap.computeIfAbsent(cacheKey, k -> new ArrayList<>())
					.add(entityDto);
			} catch (Exception e) {
				result.incrementFailCount();
				log.error("Failed to process article: {}, error: {}", article.getTitle(),
					e.getMessage(), e);
			}
		}

		return keyToArticlesMap;
	}

	private BatchSaveData identifyUniqueArticles(
		Map<String, List<ArticleEntityDto>> keyToArticlesMap, BatchProcessingResult result) {
		List<ArticleEntityDto> toSave = new ArrayList<>();
		Map<String, String> toCache = new HashMap<>();

		for (Map.Entry<String, List<ArticleEntityDto>> entry : keyToArticlesMap.entrySet()) {
			String cacheKey = entry.getKey();
			List<ArticleEntityDto> entitiesWithSameKey = entry.getValue();

			if (entitiesWithSameKey.size() > 1) {
				int batchDuplicates = entitiesWithSameKey.size() - 1;
				result.incrementBatchDuplicateCount(batchDuplicates);
				log.info("BATCH DUPLICATES: {} articles with same key '{}'",
					entitiesWithSameKey.size(), cacheKey);
			}

			ArticleEntityDto entityDto = entitiesWithSameKey.get(0);

			String cachedValue = redisTemplate.opsForValue().get(cacheKey);

			if (cachedValue != null) {
				result.incrementDuplicateCount();
				result.incrementCacheHitCount();
				log.info(
					"REDIS DUPLICATE: '{}' with key '{}', counters: duplicate={}, cacheHit={}",
					entityDto.getTitle(), cacheKey, result.getDuplicateCount(),
					result.getCacheHitCount());
			} else {
				toSave.add(entityDto);
				markForCaching(toCache, cacheKey);
			}
		}

		result.addToDuplicateCount(result.getBatchDuplicateCount());

		return new BatchSaveData(toSave, toCache);
	}

	private void saveToDatabaseAndUpdateCounters(List<ArticleEntityDto> toSave,
		BatchProcessingResult result) {
		if (!toSave.isEmpty()) {
			try {
				List<ArticleEntityDto> saved = articleRepository.saveAll(toSave);
				result.setSuccessCount(saved.size());
				sendFCM(saved); // fcm 메시지 전송 배치처리
			} catch (Exception e) {
				result.incrementFailCount(toSave.size());
				log.error("SAVE FAILED for {} articles: {}", toSave.size(), e.getMessage(), e);
			}
		} else {
			log.info("No new articles to save (all are duplicates or failed)");
		}
	}

	private void sendFCM(List<ArticleEntityDto> saved) {
		if (!saved.isEmpty()) {
			for (ArticleEntityDto articleData : saved) {
				fcmSenderOrchestrator.sendFcmWhenMailReceivedBatch(articleData.getToUserId(),
					articleData.getId(), articleData.getCreatedAt(),
					articleData.getTitle(), articleData.getFromName());
			}
		}
	}

	private void updateRedisCache(Map<String, String> toCache) {
		if (!toCache.isEmpty()) {
			for (Map.Entry<String, String> entry : toCache.entrySet()) {
				try {
					String key = entry.getKey();
					redisTemplate.opsForValue().setIfAbsent(
						key, entry.getValue(), Duration.ofMinutes(DUPLICATE_CACHE_TTL_MINUTES));
				} catch (Exception e) {
					log.error("Cache update ERROR for key '{}': {}", entry.getKey(),
						e.getMessage());
				}
			}
		}
	}

	private void logBatchSummary(int totalArticles, BatchProcessingResult result, long duration) {
		log.info(
			"ARTICLE BATCH SUMMARY: total={}, success={}, duplicate={}, cacheHit={}, failed={}, elapsed={}ms",
			totalArticles, result.getSuccessCount(), result.getDuplicateCount(),
			result.getCacheHitCount(), result.getFailCount(), duration);
	}

	private void markForCaching(Map<String, String> cacheMap, String cacheKey) {
		cacheMap.put(cacheKey, "1");
	}

	private String generateSimpleCacheKey(String fromName, String fromDomain, String title,
		UUID toUserId) {
		fromName = fromName != null ? fromName.trim().toLowerCase() : "";
		fromDomain = fromDomain != null ? fromDomain.trim().toLowerCase() : "";
		String normalizedTitle = title != null ? title.trim().toLowerCase() : "";
		String userIdStr = toUserId != null ? toUserId.toString() : "null";

		return ARTICLE_DUPLICATE_CACHE_PREFIX +
			fromName + "_" +
			fromDomain + "_" +
			userIdStr.substring(0, Math.min(8, userIdStr.length())) + "_" +
			Math.abs(normalizedTitle.hashCode());
	}
}
