package com.newzet.api.article.repository.batch;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newzet.api.article.business.dto.ArticleEntityDto;
import com.newzet.api.article.business.repository.ArticleRepository;
import com.newzet.api.article.domain.Article;
import com.newzet.api.article.repository.batch.dto.BatchProcessingResult;
import com.newzet.api.article.repository.batch.dto.BatchSaveData;
import com.newzet.api.common.batch.BatchConsumer;
import com.newzet.api.common.batch.config.BatchConfig;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleRedisBatchConsumerImpl implements BatchConsumer {

	private static final String ARTICLE_STREAM_KEY = "article:stream";
	private static final String CONSUMER_GROUP = "article-processor-group";
	private static final String CONSUMER_NAME = "article-processor";
	private static final String ARTICLE_DUPLICATE_CACHE_PREFIX = "article:dup:";
	private static final long DUPLICATE_CACHE_TTL_MINUTES = 10;

	private final RedisTemplate<String, String> redisTemplate;
	private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
	private final ArticleRepository articleRepository;
	private final BatchConfig batchConfig;
	private final ObjectMapper objectMapper;
	private ExecutorService executorService;
	private ExecutorService ackExecutorService;
	private final AtomicBoolean isProcessing = new AtomicBoolean(false);

	@PostConstruct
	public void init() {
		try {
			Boolean exists = redisTemplate.hasKey(ARTICLE_STREAM_KEY);
			if (Boolean.FALSE.equals(exists)) {
				Map<String, String> dummy = new HashMap<>();
				dummy.put("init", "init");
				redisTemplate.opsForStream().add(ARTICLE_STREAM_KEY, dummy);
				log.info("Created new Redis Stream: {}", ARTICLE_STREAM_KEY);
			}

			try {
				redisTemplate.opsForStream().createGroup(ARTICLE_STREAM_KEY, CONSUMER_GROUP);
				log.info("Created new consumer group: {} for stream: {}", CONSUMER_GROUP,
					ARTICLE_STREAM_KEY);
			} catch (Exception e) {
				log.debug("Consumer group may already exist: {}", e.getMessage());
			}
		} catch (Exception e) {
			log.error("Failed to initialize Redis Stream: {}", e.getMessage(), e);
		}
	}

	@Override
	public Map<String, Object> getBatchStatus() {
		Map<String, Object> status = new HashMap<>();
		status.put("isProcessing", isProcessing.get());
		status.put("batchSize", batchConfig.getBatchSize());

		Long pendingCount = redisTemplate.opsForStream().size(ARTICLE_STREAM_KEY);
		pendingCount = (pendingCount != null && pendingCount > 0) ? pendingCount - 1 : 0;
		status.put("pendingMessages", pendingCount != null ? pendingCount : 0);

		return status;
	}

	@Override
	public void startProcessing() {
		if (isProcessing.compareAndSet(false, true)) {
			executorService = Executors.newSingleThreadExecutor();
			ackExecutorService = Executors.newSingleThreadExecutor();
			executorService.submit(this::processBatchesAsync);
			log.info("Article batch processor started with configuration: size={}, timeout={}s",
				batchConfig.getBatchSize(), batchConfig.getTimeoutSeconds());
		}
	}

	@Override
	public void stopProcessing() {
		if (isProcessing.compareAndSet(true, false)) {
			if (executorService != null) {
				executorService.shutdownNow();
				log.info("Article batch processor stopped");
			}
			if (ackExecutorService != null) {
				ackExecutorService.shutdownNow();
			}
		}
	}

	private void processBatchesAsync() {
		log.info("Article batch processing thread initialized");

		try {
			StreamReceiver.StreamReceiverOptions<String, MapRecord<String, String, String>> options =
				StreamReceiver.StreamReceiverOptions.builder()
					.pollTimeout(Duration.ofSeconds(1))
					.build();

			StreamReceiver<String, MapRecord<String, String, String>> receiver =
				StreamReceiver.create(reactiveRedisTemplate.getConnectionFactory(), options);

			receiver.receive(
					Consumer.from(CONSUMER_GROUP,
						CONSUMER_NAME),
					StreamOffset.create(ARTICLE_STREAM_KEY, ReadOffset.lastConsumed())
				)
				.bufferTimeout(batchConfig.getBatchSize(),
					Duration.ofSeconds(batchConfig.getTimeoutSeconds()))
				.doOnNext(records -> {
					if (!records.isEmpty()) {
						log.info("Processing article batch: count={}", records.size());
						processBatchWithAck(records);
					}
				})
				.doOnError(
					error -> log.error("Stream processing error: {}", error.getMessage(), error))
				.subscribe();

			log.info("Subscribed to Redis Stream with consumer group: {}, consumer: {}",
				CONSUMER_GROUP, CONSUMER_NAME);
		} catch (Exception e) {
			log.error("Failed to initialize batch processor: {}", e.getMessage(), e);
			isProcessing.set(false);
		}
	}

	private void processBatchWithAck(List<MapRecord<String, String, String>> records) {
		List<Article> articles = new ArrayList<>();

		for (MapRecord<String, String, String> record : records) {
			try {
				String data = record.getValue().get("data");
				Article article = objectMapper.readValue(data, Article.class);
				articles.add(article);
			} catch (Exception e) {
				log.error("Failed to deserialize article from Redis stream", e);
			}
		}

		processBatchItems(articles);

		CompletableFuture.runAsync(() -> {
			List<String> ackedMessageIds = new ArrayList<>();

			for (MapRecord<String, String, String> record : records) {
				String messageId = record.getId().getValue();
				try {
					redisTemplate.opsForStream()
						.acknowledge(ARTICLE_STREAM_KEY, CONSUMER_GROUP, messageId);
					ackedMessageIds.add(messageId);
				} catch (Exception e) {
					log.warn("Ack failed for message {}, skipping for now: {}", messageId,
						e.getMessage());
				}
			}

			if (!ackedMessageIds.isEmpty()) {
				try {
					redisTemplate.opsForStream()
						.delete(ARTICLE_STREAM_KEY, ackedMessageIds.toArray(new String[0]));
				} catch (Exception e) {
					log.error("Failed to delete acked messages: {}", e.getMessage(), e);
				}
			}
		}, ackExecutorService);
	}

	private void processBatchItems(List<Article> articles) {
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

				keyToArticlesMap.computeIfAbsent(cacheKey, k -> new ArrayList<>()).add(entityDto);
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
				log.info("REDIS DUPLICATE: '{}' with key '{}', counters: duplicate={}, cacheHit={}",
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
			} catch (Exception e) {
				result.incrementFailCount(toSave.size());
				log.error("SAVE FAILED for {} articles: {}", toSave.size(), e.getMessage(), e);
			}
		} else {
			log.info("No new articles to save (all are duplicates or failed)");
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
			"BATCH SUMMARY: total={}, success={}, duplicate={}, cacheHit={}, failed={}, elapsed={}ms",
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

	@PreDestroy
	public void onShutdown() {
		stopProcessing();
	}
}
