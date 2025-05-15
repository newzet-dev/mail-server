package com.newzet.api.subscription.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.config.QuerydslConfig;
import com.newzet.api.subscription.repository.repository.SubscriptionJpaQueryRepository;
import com.newzet.api.subscription.repository.repository.SubscriptionRepositoryImpl;

@DataJpaTest
@Import({SubscriptionJpaQueryRepository.class, SubscriptionRepositoryImpl.class, QuerydslConfig.class})
@ExtendWith({PostgresTestContainerConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SubscriptionJpaQueryRepositoryTest {

	private static final UUID userId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
	private static final String name = "testName";
	private static final String domain = "testDomain";
	private static final String mailingList = "testMailingList";

	@Autowired
	SubscriptionRepositoryImpl subscriptionRepositoryImpl;
	@Autowired
	SubscriptionJpaQueryRepository jpaQueryRepository;

	@Test
	public void isSubscribe_whenAlreadySubscribed_returnTrue() {
		//Given
		UUID fakeUserId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12");
		String fakeDomain = "fakeDomain";
		String fakeMailingList = "fakeMailingList";
		subscriptionRepositoryImpl.save(userId, name, domain, mailingList);

		//When& Then
		assertFalse(jpaQueryRepository.isSubscribe(fakeUserId, domain, mailingList));
		assertFalse(jpaQueryRepository.isSubscribe(userId, fakeDomain, fakeMailingList));
		assertTrue(jpaQueryRepository.isSubscribe(userId, fakeDomain, mailingList));
		assertTrue(jpaQueryRepository.isSubscribe(userId, domain, fakeMailingList));
		assertTrue(jpaQueryRepository.isSubscribe(userId, domain, mailingList));
	}

	@Test
	public void isSubscribe_whenUnSubscribed_returnFalse() {
		//When & Then
		assertFalse(jpaQueryRepository.isSubscribe(userId, domain, mailingList));
	}
}
