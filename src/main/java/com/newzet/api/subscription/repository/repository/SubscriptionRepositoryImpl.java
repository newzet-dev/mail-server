package com.newzet.api.subscription.repository.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.repository.NewsletterEntity;
import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;
import com.newzet.api.subscription.business.service.SubscriptionRepository;
import com.newzet.api.subscription.repository.entity.SubscriptionEntity;
import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.user.repository.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

	private final SubscriptionJpaRepository subscriptionJpaRepository;

	@Override
	public SubscriptionEntityDto save(UserEntityDto userDto, NewsletterEntityDto newsletterDto) {
		UserEntity user = UserEntity.create(userDto.getId(), userDto.getEmail(),
			userDto.getStatus());
		NewsletterEntity newsletter = NewsletterEntity.create(newsletterDto.getId(),
			newsletterDto.getName(), newsletterDto.getDomain(), newsletterDto.getMailingList(),
			newsletterDto.getStatus());
		SubscriptionEntity subscription = subscriptionJpaRepository.save(
			SubscriptionEntity.create(user, newsletter, LocalDateTime.now(), null));

		NewsletterEntityDto newsletterEntityDto = subscription.getNewsletter().toEntityDto();
		return SubscriptionEntityDto.create(subscription.getId(),
			newsletterEntityDto, subscription.getCreatedAt());
	}

	@Override
	public Optional<SubscriptionEntityDto> findByUserIdAndNewsletterId(Long userId,
		Long newsletterId) {
		return null;
	}
}
