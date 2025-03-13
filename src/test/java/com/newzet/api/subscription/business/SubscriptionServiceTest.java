package com.newzet.api.subscription.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.repository.NewsletterEntity;
import com.newzet.api.newsletter.repository.NewsletterJpaRepository;
import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;
import com.newzet.api.subscription.business.service.SubscriptionRepository;
import com.newzet.api.subscription.business.service.SubscriptionService;
import com.newzet.api.subscription.domain.Subscription;
import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.user.domain.User;
import com.newzet.api.user.repository.entity.UserEntity;
import com.newzet.api.user.repository.repository.UserJpaRepository;

@SpringBootTest
@Transactional
@ExtendWith(PostgresTestContainerConfig.class)
public class SubscriptionServiceTest {

	@Autowired
	private UserJpaRepository userRepository;

	@Autowired
	private NewsletterJpaRepository newsletterRepository;

	@Autowired
	private SubscriptionService subscriptionService;

	@MockitoSpyBean
	private SubscriptionRepository subscriptionRepository;

	private static User user;
	private static Newsletter newsletter;

	@BeforeEach
	public void setUp() {
		UserEntity userEntity = userRepository.save(
			UserEntity.create("test@example.com", "ACTIVE"));
		user = UserEntityDto.create(userEntity.getId(), userEntity.getEmail(),
			userEntity.getStatus().name()).toDomain();

		NewsletterEntity newsletterEntity = newsletterRepository.save(
			NewsletterEntity.create("test",
				"test@example.com", "test123", "REGISTERED"));
		newsletter = NewsletterEntityDto.create(newsletterEntity.getId(),
			newsletterEntity.getName(),
			newsletterEntity.getDomain(), newsletterEntity.getMailingList(),
			newsletterEntity.getStatus().name()).toDomain();
	}

	@Test
	public void addSubscription_whenSubscriptionNoExist_createNewSubscription() {
		//When
		subscriptionService.addSubscription(user, newsletter);

		//Then
		verify(subscriptionRepository, times(1)).create(any(), any());
		verify(subscriptionRepository, never()).save(any());
	}

	@Test
	public void subscribe_whenSubscription_doNothing() {
		//Given
		SubscriptionEntityDto subscriptionDto = subscriptionRepository.create(
			user.toEntityDto(), newsletter.toEntityDto());

		//When
		subscriptionService.addSubscription(user, newsletter);

		//Then
		Subscription reactivatedSubscription = subscriptionRepository.getById(
			subscriptionDto.getId()).toDomain();

		verify(subscriptionRepository, times(1)).save(any());
		verify(subscriptionRepository, times(1)).create(any(), any());
		assertEquals(subscriptionDto.getDeletedAt(), reactivatedSubscription.getDeletedAt());
		assertEquals(subscriptionDto.getCreatedAt(), reactivatedSubscription.getCreatedAt());
	}

	@Test
	public void subscribe_whenSubscriptionIsDeleted_updateDeletedAtIsNull() {
		//Given
		SubscriptionEntityDto subscriptionDto = subscriptionRepository.create(
			user.toEntityDto(), newsletter.toEntityDto());
		subscriptionService.deleteSubscription(subscriptionDto.getId());

		//When
		subscriptionService.addSubscription(user, newsletter);

		//Then
		Subscription reactivatedSubscription = subscriptionRepository.getById(
			subscriptionDto.getId()).toDomain();

		verify(subscriptionRepository, times(2)).save(any());
		verify(subscriptionRepository, times(1)).create(any(), any());
		assertNull(reactivatedSubscription.getDeletedAt());
		assertEquals(subscriptionDto.getCreatedAt(), reactivatedSubscription.getCreatedAt());
	}

	@Test
	public void deleteSubscription() {
		//Given
		SubscriptionEntityDto subscriptionDto = subscriptionRepository.create(
			user.toEntityDto(), newsletter.toEntityDto());

		//When
		subscriptionService.deleteSubscription(subscriptionDto.getId());

		//Then
		Subscription deletedSubscription = subscriptionRepository.getById(
			subscriptionDto.getId()).toDomain();

		assertNotNull(deletedSubscription.getDeletedAt());
		assertNotEquals(subscriptionDto.getDeletedAt(), deletedSubscription.getDeletedAt());
		assertEquals(subscriptionDto.getCreatedAt(), deletedSubscription.getCreatedAt());
	}
}
