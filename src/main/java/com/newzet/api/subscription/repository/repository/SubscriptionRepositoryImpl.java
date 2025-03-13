package com.newzet.api.subscription.repository.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.newsletter.business.dto.NewsletterEntityDto;
import com.newzet.api.newsletter.repository.NewsletterEntity;
import com.newzet.api.subscription.business.dto.SubscriptionEntityDto;
import com.newzet.api.subscription.business.service.SubscriptionRepository;
import com.newzet.api.subscription.repository.entity.SubscriptionEntity;
import com.newzet.api.subscription.repository.exception.NoSubscriptionException;
import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.user.repository.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

	private final SubscriptionJpaRepository subscriptionJpaRepository;

	@Override
	public SubscriptionEntityDto create(UserEntityDto userDto, NewsletterEntityDto newsletterDto) {
		UserEntity user = UserEntity.create(userDto.getId(), userDto.getEmail(),
			userDto.getStatus());
		NewsletterEntity newsletter = NewsletterEntity.create(newsletterDto.getId(),
			newsletterDto.getName(), newsletterDto.getDomain(), newsletterDto.getMailingList(),
			newsletterDto.getStatus());

		SubscriptionEntity subscriptionEntity = SubscriptionEntity.create(user, newsletter,
			LocalDateTime.now(), null);
		return subscriptionJpaRepository.save(subscriptionEntity).toEntityDto();
	}

	@Override
	public Optional<SubscriptionEntityDto> findByUserIdAndNewsletterId(Long userId,
		Long newsletterId) {
		return subscriptionJpaRepository.findByUserIdAndNewsletterId(userId, newsletterId)
			.map(SubscriptionEntity::toEntityDto);
	}

	@Override
	public void save(SubscriptionEntityDto subscriptionDto) {
		SubscriptionEntity subscriptionEntity = subscriptionJpaRepository.findById(
				subscriptionDto.getId())
			.orElseThrow(() -> new NoSubscriptionException(this.getClass().getSimpleName() + "#" +
				Thread.currentThread().getStackTrace()[2].getMethodName()));
		subscriptionEntity.update(subscriptionDto.getCreatedAt(), subscriptionDto.getDeletedAt());
	}

	@Override
	public SubscriptionEntityDto getById(UUID id) {
		return subscriptionJpaRepository.findById(id)
			.orElseThrow(() -> new NoSubscriptionException(this.getClass().getSimpleName() + "#" +
				Thread.currentThread().getStackTrace()[2].getMethodName())).toEntityDto();
	}
}
