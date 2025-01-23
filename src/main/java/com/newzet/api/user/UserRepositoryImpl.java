package com.newzet.api.user;

import java.util.List;

import com.newzet.api.exception.user.NoActiveUserException;
import com.newzet.api.subscription.SubscriptionJpaEntity;
import com.newzet.api.subscription.SubscriptionJpaRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;
	private final SubscriptionJpaRepository subscriptionJpaRepository;

	@Override
	public ActiveUser findActiveUserByEmail(String email) {
		UserJpaEntity userJpaEntity = userJpaRepository.findByEmailAndStatus(email,
			UserStatus.ACTIVE).orElseThrow(NoActiveUserException::new);

		// TODO: SubscriptionJpaEntity 조회
		List<SubscriptionJpaEntity> subscriptionJpaEntity = subscriptionJpaRepository
			.fineAllByUserId(userJpaEntity.getId());

		// TODO: NewsletterJpaEntity 조회
		return null;
	}
}
