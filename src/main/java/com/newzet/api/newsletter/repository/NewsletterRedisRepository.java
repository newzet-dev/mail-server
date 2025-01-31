package com.newzet.api.newsletter.repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newzet.api.newsletter.exception.RedisSerializationException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsletterRedisRepository implements NewsletterCacheRepository {

	private static final Long NEWSLETTER_DURATION = 1000L * 60 * 60;
	private static final String DOMAIN_PREFIX = "newsletter:domain";
	private static final String MAILING_LIST_PREFIX = "newsletter:ml";
	private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public void saveByDomain(String domain, NewsletterEntity newsletterEntity) {
		redisTemplate.opsForValue().set(DOMAIN_PREFIX + domain,
			serialize(newsletterEntity), NEWSLETTER_DURATION, TIME_UNIT);
	}

	@Override
	public void saveByMailingList(String mailingList, NewsletterEntity newsletterEntity) {

	}

	@Override
	public Optional<NewsletterEntity> findByDomain(String domain) {
		String value = redisTemplate.opsForValue().get(DOMAIN_PREFIX + domain);
		return deserialize(value);
	}

	@Override
	public Optional<NewsletterEntity> findByMailingList(String mailingList) {
		return Optional.empty();
	}

	@Override
	public void deleteAll() {

	}

	private String serialize(NewsletterEntity newsletterEntity) {
		try {
			return objectMapper.writeValueAsString(newsletterEntity);
		} catch (JsonProcessingException e) {
			throw new RedisSerializationException("newsletterEntity 직렬화 오류", e);
		}
	}

	private Optional<NewsletterEntity> deserialize(String value) {
		if (!StringUtils.hasText(value)) {
			return Optional.empty();
		}
		try {
			return Optional.of(objectMapper.readValue(value, NewsletterEntity.class));
		} catch (JsonProcessingException e) {
			throw new RedisSerializationException("newsletterEntity 역직렬화 오류", e);
		}
	}
}
