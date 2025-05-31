package com.newzet.api.article.repository.batch;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newzet.api.article.domain.Article;
import com.newzet.api.common.batch.BatchProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleRedisBatchProducerImpl implements BatchProducer {

	private static final String ARTICLE_STREAM_KEY = "article:stream";
	private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public void addToBatch(Article article) {
		try {
			String jsonData = objectMapper.writeValueAsString(article);

			Map<String, String> fields = new HashMap<>();
			fields.put("data", jsonData);

			reactiveRedisTemplate.opsForStream()
				.add(ARTICLE_STREAM_KEY, fields)
				.subscribeOn(Schedulers.boundedElastic())
				.doOnSuccess(recordId -> {
					if (log.isDebugEnabled()) {
						log.debug("Article added to batch queue: {}, recordId: {}",
							article.getTitle(), recordId);
					}
				})
				.doOnError(error ->
					log.error("Failed to add article to stream: {}, error: {}",
						article.getTitle(), error.getMessage(), error)
				)
				.subscribe();
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize article DTO: {}", e.getMessage());
		}
	}
}
