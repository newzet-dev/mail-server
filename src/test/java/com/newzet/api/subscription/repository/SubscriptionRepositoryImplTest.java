package com.newzet.api.subscription.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.repository.NewsletterEntity;
import com.newzet.api.newsletter.repository.NewsletterJpaRepository;
import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;
import com.newzet.api.subscription.repository.repository.SubscriptionRepositoryImpl;
import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.user.repository.entity.UserEntity;
import com.newzet.api.user.repository.repository.UserJpaRepository;

@DataJpaTest
@Import(SubscriptionRepositoryImpl.class)
@ExtendWith(PostgresTestContainerConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SubscriptionRepositoryImplTest {

	@Autowired
	private SubscriptionRepositoryImpl subscriptionRepository;

	@Autowired
	private UserJpaRepository userRepository;

	@Autowired
	private NewsletterJpaRepository newsletterRepository;

	private static UserEntityDto userDto;
	private static NewsletterEntityDto newsletterDto;

	@BeforeEach
	public void setUp() {
		UserEntity user = userRepository.save(UserEntity.create("test@example.com", "ACTIVE"));
		userDto = UserEntityDto.create(user.getId(), user.getEmail(), user.getStatus().name());

		NewsletterEntity newsletter = newsletterRepository.save(NewsletterEntity.create("test",
			"test@example.com", "test123", "REGISTERED"));
		newsletterDto = NewsletterEntityDto.create(newsletter.getId(), newsletter.getName(),
			newsletter.getDomain(), newsletter.getMailingList(), newsletter.getStatus().name());
	}

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
