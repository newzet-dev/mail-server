package com.newzet.api.subscription.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.newzet.api.category.repository.CategoryEntity;
import com.newzet.api.category.repository.CategoryJpaRepository;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.newsletter.fixture.NewsletterFixture;
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

	private static UserEntityDto userDto;
	private static NewsletterEntity newsletterEntity;
	private static CategoryEntity categoryEntity;
	@Autowired
	private SubscriptionRepositoryImpl subscriptionRepository;
	@Autowired
	private UserJpaRepository userRepository;
	@Autowired
	private NewsletterJpaRepository newsletterRepository;
	@Autowired
	private CategoryJpaRepository categoryRepository;

	@BeforeEach
	public void setUp() {
		UserEntity user = userRepository.save(UserEntity.create("test@example.com", "test","ACTIVE"));
		userDto = UserEntityDto.create(user.getId(), user.getEmail(), user.getStatus().name());

		categoryEntity = categoryRepository.save(CategoryEntity.create("test", "test", "test"));
		newsletterEntity = newsletterRepository.save(NewsletterFixture.createDefaultEntity(categoryEntity));
	}

	@Test
	public void create_returnSubscriptionEntityDto() {
		//When
		SubscriptionEntityDto subscription = subscriptionRepository.create(userDto,
			newsletterEntity);

		//Then
		assertTrue(subscriptionRepository.findByUserIdAndNewsletterId(
			userDto.getId(), newsletterEntity.getId()).isPresent());
	}

	@Test
	public void findByUserIdAndNewsletterId_whenExist_returnSubscriptionEntityDto() {
		//Given
		SubscriptionEntityDto subscription = subscriptionRepository.create(userDto,
			newsletterEntity);

		//When, Then
		assertTrue(subscriptionRepository.findByUserIdAndNewsletterId(
			userDto.getId(), newsletterEntity.getId()).isPresent());
	}

	@Test
	public void findByUserIdAndNewsletterId_whenNoExist_returnOptionalEmpty() {
		//When
		Optional<SubscriptionEntityDto> foundedSubscription = subscriptionRepository.findByUserIdAndNewsletterId(
			userDto.getId(), newsletterEntity.getId());

		//Then
		assertTrue(foundedSubscription.isEmpty());
	}

	@Test
	public void save() {
		//Given
		SubscriptionEntityDto original = subscriptionRepository.create(userDto,
			newsletterEntity);
		SubscriptionEntityDto changed = SubscriptionEntityDto.create(
			original.getId(), LocalDateTime.MAX, LocalDateTime.MIN);

		//When
		subscriptionRepository.save(changed);

		//Then
		assertEquals(LocalDateTime.MAX, changed.getCreatedAt());
		assertEquals(LocalDateTime.MIN, changed.getDeletedAt());
	}
}
