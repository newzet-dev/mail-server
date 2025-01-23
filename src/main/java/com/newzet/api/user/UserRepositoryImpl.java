package com.newzet.api.user;

import java.util.List;

import org.springframework.stereotype.Component;

import com.newzet.api.exception.NewsletterNotFoundException;
import com.newzet.api.exception.user.NoActiveUserException;
import com.newzet.api.newsletter.NewsletterJpaEntity;
import com.newzet.api.newsletter.NewsletterJpaRepository;
import com.newzet.api.subscription.SubscriptionJpaEntity;
import com.newzet.api.subscription.SubscriptionJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;
	private final SubscriptionJpaRepository subscriptionJpaRepository;
	private final NewsletterJpaRepository newsletterJpaRepository;

	// TODO: JPQL로 성능 개선, findAllBy 활용해도 됨
	@Override
	public ActiveUser findActiveUserByEmail(String email) {
		UserJpaEntity userJpaEntity = userJpaRepository.findByEmailAndStatus(email,
			UserStatus.ACTIVE).orElseThrow(NoActiveUserException::new);

		List<SubscriptionJpaEntity> subscriptionJpaEntityList = subscriptionJpaRepository
			.findAllByUserId(userJpaEntity.getId());

		List<NewsletterJpaEntity> newsletterJpaEntityList = subscriptionJpaEntityList.stream()
			.map(subscription -> newsletterJpaRepository.findById(subscription.getId())
				.orElseThrow(() -> new NewsletterNotFoundException("id에 해당하는 뉴스레터가 없습니다")))
			.toList();

		// TODO User 조립
		return null;
	}
}
