package com.newzet.api.article.repository.batch;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import com.newzet.api.article.business.batch.ArticleBatchProducer;
import com.newzet.api.article.domain.Article;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleRedisBatchProducerImpl implements ArticleBatchProducer {

	private static final String ARTICLE_STREAM_KEY = "article:stream";
	private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
	private final OptionalObjectMapper optionalObjectMapper;

	@Override
	public void addToBatch(Article article) {
		String jsonData = optionalObjectMapper.serialize(article);

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
	}
}
