package com.newzet.api.newsletter.business;

import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.common.cache.CacheUtil;
import com.newzet.api.config.JwtTestConfig;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.config.RedisTestContainerConfig;
import com.newzet.api.newsletter.business.repository.NewsletterRepository;
import com.newzet.api.newsletter.business.service.NewsletterService;
import com.newzet.api.newsletter.fixture.NewsletterFixture;
import com.newzet.api.newsletter.repository.NewsletterJpaRepository;
import com.newzet.api.newsletter.repository.entity.NewsletterEntity;

@SpringBootTest
@ComponentScan(basePackages = {"com.newzet.api.newsletter", "com.newzet.api.common"})
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith({RedisTestContainerConfig.class, PostgresTestContainerConfig.class,
	JwtTestConfig.class})
public class NewsletterServiceConcurrencyTest {

	private final String name = "test";
	private final String domain = "test@example.com";
	private final String mailingList = "test123";
	@Autowired
	private NewsletterService newsletterService;
	@Autowired
	private CacheUtil cacheUtil;
	@MockitoSpyBean
	private NewsletterRepository newsletterRepository;
	private NewsletterEntity newsletterEntity = NewsletterFixture.createDefaultEntity();
	@MockitoBean
	private NewsletterJpaRepository newsletterJpaRepository;

	@Test
	public void findOrCreateNewsletter_when100Requests_requestDB1time() throws
		InterruptedException {
		//Given
		int numberOfThreads = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);
		when(newsletterJpaRepository.save(any())).thenReturn(newsletterEntity);
		//When
		for (int i = 0; i < numberOfThreads; i++) {
			executorService.submit(() -> {
				try {
					newsletterService.findOrCreateNewsletter(name, domain, mailingList);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		//Then
		verify(newsletterRepository, times(1)).findByDomainOrMailingList(domain, mailingList);
	}
}
