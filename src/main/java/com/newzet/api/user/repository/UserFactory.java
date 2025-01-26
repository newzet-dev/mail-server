package com.newzet.api.user.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import com.newzet.api.newsletter.repository.NewsletterEntityRepository;
import com.newzet.api.newsletter.repository.NewsletterJpaEntity;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.user.domain.ActiveUser;
import com.newzet.api.user.domain.MailReciepient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserFactory implements UserRepository {

	private final UserEntityRepository userEntityRepository;
	private final NewsletterEntityRepository newsletterEntityRepository;

	@Override
	public MailReciepient findMailRecipientByEmail(String email) {
		UserJpaEntity userJpaEntity = userEntityRepository.findActiveUserByEmail(email);
		List<NewsletterJpaEntity> newsletterJpaEntityList = newsletterEntityRepository
			.findNewsletterListByUserId(userJpaEntity.getId());
		List<Newsletter> newsletterList = newsletterJpaEntityList.stream()
			.map(NewsletterJpaEntity::toNewsletter)
			.toList();

		return ActiveUser.create(userJpaEntity.getEmail(), newsletterList);
	}
}
