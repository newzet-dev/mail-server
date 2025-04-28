package com.newzet.api.mail.business;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.newsletter.business.NewsletterService;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.subscription.business.service.SubscriptionService;
import com.newzet.api.user.business.service.UserService;
import com.newzet.api.user.domain.User;

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
		User user = userService.getUserByEmail(toDomain);
		Newsletter newsletter = newsletterService.findOrCreateNewsletter(fromName,
			fromDomain, mailingList);
		subscriptionService.addSubscription(user, newsletter);
	}
}
