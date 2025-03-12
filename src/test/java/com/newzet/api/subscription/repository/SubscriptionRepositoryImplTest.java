package com.newzet.api.subscription.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;
import com.newzet.api.subscription.repository.repository.SubscriptionRepositoryImpl;
import com.newzet.api.user.business.dto.UserEntityDto;

@DataJpaTest
@Import(SubscriptionRepositoryImpl.class)
@ExtendWith(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SubscriptionRepositoryImplTest {

	@Autowired
	private SubscriptionRepositoryImpl subscriptionRepository;

	@Test
	public void save_returnSubscriptionEntityDto() {
		//Given
		Long id = 1L;
		String name = "test";
		String domain = "test@example.com";
		String mailingList = "test123";
		String status = "REGISTERED";

		UserEntityDto userDto = UserEntityDto.create(1L, "test@example.com", "ACTIVE");
		NewsletterEntityDto newsletterDto = NewsletterEntityDto.create(id, name, domain,
			mailingList, status);

		//When
		SubscriptionEntityDto subscription = subscriptionRepository.save(userDto,
			newsletterDto);

		//Then
		NewsletterEntityDto newsletter = subscription.getNewsletterEntityDto();
		assertEquals(id, newsletter.getId());
		assertEquals(name, newsletter.getName());
		assertEquals(domain, newsletter.getDomain());
		assertEquals(mailingList, newsletter.getMailingList());
		assertEquals(status, newsletter.getStatus());
	}
}
