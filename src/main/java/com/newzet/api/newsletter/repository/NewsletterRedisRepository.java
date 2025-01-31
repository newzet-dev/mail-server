package com.newzet.api.newsletter.repository;

import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NewsletterRedisRepository implements NewsletterCacheRepository {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public void saveByDomain(String domain, NewsletterEntity newsletterEntity) {

	}

	@Override
	public void saveByMailingList(String mailingList, NewsletterEntity newsletterEntity) {

	}

	@Override
	public Optional<NewsletterEntity> findByDomain(String domain) {
		return Optional.empty();
	}

	@Override
	public Optional<NewsletterEntity> findByMailingList(String mailingList) {
		return Optional.empty();
	}

	@Override
	public void deleteAll() {

	}
}
