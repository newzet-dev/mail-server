package com.newzet.api.subscription.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

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

	private final UserEntityDto userDto = UserEntityDto.create(1L, "test@example.com", "ACTIVE");
	private final NewsletterEntityDto newsletterDto = NewsletterEntityDto.create(1L, "test",
		"test@example.com", "test123", "REGISTERED");

	@Test
	public void save_returnSubscriptionEntityDto() {
		//When
		SubscriptionEntityDto subscription = subscriptionRepository.save(userDto,
			newsletterDto);

		//Then
		compareNewsletterEntity(newsletterDto, subscription.getNewsletterEntityDto());
	}

	@Test
	public void findByUserIdAndNewsletterId_whenExist_returnSubscriptionEntityDto() {
		//Given
		SubscriptionEntityDto subscription = subscriptionRepository.save(userDto,
			newsletterDto);

		//When
		Optional<SubscriptionEntityDto> foundedSubscription = subscriptionRepository.findByUserIdAndNewsletterId(
			userDto.getId(), newsletterDto.getId());

		//Then
		assertTrue(foundedSubscription.isPresent());
		compareNewsletterEntity(newsletterDto, foundedSubscription.get().getNewsletterEntityDto());
	}

	@Test
	public void findByUserIdAndNewsletterId_whenNoExist_returnOptionalEmpty() {
		//When
		Optional<SubscriptionEntityDto> foundedSubscription = subscriptionRepository.findByUserIdAndNewsletterId(
			userDto.getId(), newsletterDto.getId());

		//Then
		assertTrue(foundedSubscription.isEmpty());
	}

	private static void compareNewsletterEntity(NewsletterEntityDto expected,
		NewsletterEntityDto actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getDomain(), actual.getDomain());
		assertEquals(expected.getMailingList(), actual.getMailingList());
		assertEquals(expected.getStatus(), actual.getStatus());
	}
}
