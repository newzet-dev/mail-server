package com.newzet.api.newsletter.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.newsletter.domain.NewsletterStatus;
import com.newzet.api.subscription.repository.SubscriptionJpaEntity;
import com.newzet.api.user.domain.UserStatus;
import com.newzet.api.user.repository.UserJpaEntity;
import com.newzet.api.user.repository.UserJpaRepository;

@SpringBootTest
@Transactional
class NewsletterJpaRepositoryCustomImplTest {

	@Autowired
	private NewsletterJpaRepositoryCustomImpl newsletterJpaRepositoryCustom;

	@Autowired
	private UserJpaRepository userJpaRepository;

	@Autowired
	private NewsletterJpaRepository newsletterJpaRepository;

	@Autowired
	private SubscriptionJpaRepository subscriptionJpaRepository;

	@Test
	public void userId에_해당하는_뉴스레터가_존재하면_해당_리스트를_반환() {
		//Given
		setUp();

		//When
		List<NewsletterJpaEntity> findNewsletterList = newsletterJpaRepositoryCustom
			.findAllByUserId(1L);

		//Then
		assertNotNull(findNewsletterList);
		assertEquals(2, findNewsletterList.size());
	}

	@Test
	public void userId에_해당하는_뉴스레터가_없으면_빈_리스트를_반환() {
		//Given
		setUp();

		//When
		List<NewsletterJpaEntity> findNewsletterList = newsletterJpaRepositoryCustom
			.findAllByUserId(3L);

		//Then
		assertNotNull(findNewsletterList);
		assertEquals(0, findNewsletterList.size());
	}

	UserJpaEntity setUp() {
		UserJpaEntity user1 = new UserJpaEntity("test1@example.com", UserStatus.ACTIVE);
		UserJpaEntity user2 = new UserJpaEntity("test2@example.com", UserStatus.ACTIVE);
		UserJpaEntity user3 = new UserJpaEntity("test3@example.com", UserStatus.ACTIVE);
		userJpaRepository.saveAll(List.of(user1, user2, user3));

		NewsletterJpaEntity newsletter1 = new NewsletterJpaEntity(NewsletterStatus.REGISTERED);
		NewsletterJpaEntity newsletter2 = new NewsletterJpaEntity(NewsletterStatus.UNREGISTERED);
		newsletterJpaRepository.saveAll(List.of(newsletter1, newsletter2));

		List<SubscriptionJpaEntity> addedSubscriptionList = List.of(
			new SubscriptionJpaEntity(user1, newsletter1),
			new SubscriptionJpaEntity(user1, newsletter2),
			new SubscriptionJpaEntity(user2, newsletter1)
		);

		subscriptionJpaRepository.saveAll(addedSubscriptionList);
		return user1;
	}
}
