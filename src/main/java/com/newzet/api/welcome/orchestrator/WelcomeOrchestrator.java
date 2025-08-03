package com.newzet.api.welcome.orchestrator;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	private static final UUID NEWZET_NEWSLETTER_ID = UUID.fromString("c4922e54-f58a-4270-80da-2dc6d59bc4fa");
	private static final String WELCOME_MAIL_TITLE = "💌 뉴젯과 더욱 친해지는 방법 💌";
	private static final String WELCOME_MAIL_URL = "newzet_content/welcome_letter";
	private static final String WELCOME_MAIL_IMAGE_URL = "https://newzet-lib.s3.ap-northeast-2.amazonaws.com/newsletter-image/trend_issue/nz_logo.webp";

	private final NewsletterService newsletterService;
	private final ArticleService articleService;
	private final SubscriptionService subscriptionService;
	private final FcmSenderService fcmSenderService;

	public void sendWelcomeMail(UUID userId) {
		Newsletter newsletter = newsletterService.findNewsLetterById(NEWZET_NEWSLETTER_ID);
		Article article = articleService.addArticle(userId, newsletter.getName(), newsletter.getDomain(),
			WELCOME_MAIL_TITLE, WELCOME_MAIL_URL, WELCOME_MAIL_IMAGE_URL, newsletter.getMailingList());
		subscriptionService.addSubscriptionIfUnsubscribed(userId, newsletter.getName(), newsletter.getDomain(),
			newsletter.getMailingList());
		fcmSenderService.sendFcmNotBatch(userId, article.getId(), article.getCreatedAt(), article.getTitle(),
			newsletter.getName());
	}
}
