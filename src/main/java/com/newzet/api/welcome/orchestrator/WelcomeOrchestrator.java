package com.newzet.api.welcome.orchestrator;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Value;
import com.newzet.api.article.business.service.ArticleService;
import com.newzet.api.article.domain.Article;
import com.newzet.api.fcm.business.service.FcmSenderService;
import com.newzet.api.newsletter.business.service.NewsletterService;
import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.subscription.business.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WelcomeOrchestrator {
	
	private static final String WELCOME_MAIL_TITLE = "💌 뉴젯과 더욱 친해지는 방법 💌";
	private static final String WELCOME_MAIL_URL = "welcome_letter";
	private final NewsletterService newsletterService;
	private final ArticleService articleService;
	private final SubscriptionService subscriptionService;
	private final FcmSenderService fcmSenderService;
	@Value("${newzet.newsletter.id}")
	private String newzetNewsletterId;

	public void sendWelcomeMail(UUID userId) {
		Newsletter newsletter = newsletterService.findNewsLetterById(UUID.fromString(newzetNewsletterId));
		Article article = articleService.addArticle(userId, newsletter.getName(), newsletter.getDomain(),
			WELCOME_MAIL_TITLE, WELCOME_MAIL_URL, newsletter.getImageUrl(), newsletter.getMailingList());
		subscriptionService.addSubscriptionIfUnsubscribed(userId, newsletter.getName(), newsletter.getDomain(),
			newsletter.getMailingList());
		fcmSenderService.sendFcmNotBatch(userId, article.getId(), article.getCreatedAt(), article.getTitle(),
			newsletter.getName());
	}
}
