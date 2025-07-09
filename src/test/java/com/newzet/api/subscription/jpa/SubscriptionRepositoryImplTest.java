package com.newzet.api.subscription.jpa;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.subscription.domain.Subscription;
import com.newzet.api.subscription.jpa.repository.SubscriptionRepositoryImpl;

@DataJpaTest
@Import({SubscriptionRepositoryImpl.class})
@ExtendWith({PostgresTestContainerConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SubscriptionRepositoryImplTest {

	private static final UUID userId = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
	private static final String name = "testName";
	private static final String domain = "testDomain";
	private static final String mailingList = "testMailingList";

	@Autowired
	private SubscriptionRepositoryImpl repositoryImpl;

	@Test
	public void save() {
		//When
		Subscription savedSubscription = repositoryImpl
			.save(userId, name, domain, mailingList);

		//Then
		assertEquals(userId, savedSubscription.getUserId());
		assertEquals(name, savedSubscription.getNewsletterName());
		assertEquals(domain, savedSubscription.getNewsletterDomain());
		assertEquals(mailingList, savedSubscription.getNewsletterMailingList());
	}
}
