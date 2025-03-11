package com.newzet.api.newsletter.business;

import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.newzet.api.common.cache.CacheUtil;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.config.RedisTestContainerConfig;

@SpringBootTest
@ExtendWith({RedisTestContainerConfig.class, PostgresTestContainerConfig.class})
public class NewsletterServiceConcurrencyTest {

	@Autowired
	private NewsletterService newsletterService;

	@Autowired
	private CacheUtil cacheUtil;

	@MockitoSpyBean
	private NewsletterRepository newsletterRepository;

	private final String name = "test";
	private final String domain = "test@example.com";
	private final String mailingList = "test123";

	@BeforeEach
	void setUp() {
		cacheUtil.deleteAllKeys();
	}

	@Test
	public void findOrCreateNewsletter_when100Requests_requestDB1time() throws
		InterruptedException {
		//Given
		int numberOfThreads = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(numberOfThreads);

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
