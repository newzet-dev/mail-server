package com.newzet.api.article.repository.batch;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import com.newzet.api.article.business.batch.ArticleBatchProducer;
import com.newzet.api.article.domain.Article;
import com.newzet.api.common.batch.RedisBatchProducer;
import com.newzet.api.common.objectMapper.OptionalObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ArticleRedisBatchProducerImpl extends RedisBatchProducer<Article>
	implements ArticleBatchProducer {

	private static final String ARTICLE_STREAM_KEY = "article:stream";

	public ArticleRedisBatchProducerImpl(
		ReactiveRedisTemplate<String, String> reactiveRedisTemplate,
		OptionalObjectMapper optionalObjectMapper) {
		super(reactiveRedisTemplate, optionalObjectMapper);
	}

	@Override
	protected String getStreamKey() {
		return ARTICLE_STREAM_KEY;
	}

	@Override
	protected String getItemTypeName() {
		return "Article";
	}

	@Override
	protected String getItemIdentifier(Article article) {
		return article.getTitle();
	}
}
