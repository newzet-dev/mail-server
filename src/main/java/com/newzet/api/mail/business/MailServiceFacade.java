package com.newzet.api.mail.business;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.newsletter.business.service.NewsletterService;
import com.newzet.api.subscription.business.service.SubscriptionService;
import com.newzet.api.user.business.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MailServiceFacade {

	private final UserService userService;
	private final NewsletterService newsletterService;
	private final SubscriptionService subscriptionService;

	public void processMail(String fromName, String fromDomain, String toDomain, String mailingList,
		String htmlLink) {
		UUID userId = userService.getUserIdByEmail(toDomain);
		subscriptionService.addSubscriptionIfUnsubscribed(userId, fromName, fromDomain, mailingList);

		//TODO: Article 저장 구현 (배치처리 + 비동기)
		//TODO: 아티클 저장 후 배치처리 된 애들에 대해서 별도로 event 형식으로 fcm noti 보내는거 추가 (여기 말고)
	}
}
