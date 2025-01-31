package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newzet.api.newsletter.config.EmbeddedRedisConfig;

@DataRedisTest
@Import(EmbeddedRedisConfig.class)
class NewsletterRedisRepositoryTest {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	private NewsletterRedisRepository newsletterRedisRepository;

	@BeforeEach
	void setUp() {
		newsletterRedisRepository = new NewsletterRedisRepository(redisTemplate,
			new ObjectMapper());
	}

	@AfterEach
	void teardown() {
		newsletterRedisRepository.deleteAll();
	}

	@Test
	public void findByDomain_존재하면_NewsletterEntity를_반환() {
		//Given
		String domain = "exist@example.com";
		NewsletterEntity newsletter = NewsletterEntity.builder()
			.domain(domain)
			.build();
		newsletterRedisRepository.saveByDomain(domain, newsletter);

		//When
		Optional<NewsletterEntity> foundNewsletter = newsletterRedisRepository.findByDomain(domain);

		//Then
		assertTrue(foundNewsletter.isPresent());
		assertEquals(domain, foundNewsletter.get().getDomain());
	}

}
