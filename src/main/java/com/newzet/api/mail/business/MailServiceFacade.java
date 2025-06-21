package com.newzet.api.mail.business;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.article.business.service.ArticleService;
import com.newzet.api.subscription.business.service.SubscriptionService;
import com.newzet.api.userinfo.business.service.UserinfoService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MailServiceFacade {

	private final UserinfoService userinfoService;
	private final SubscriptionService subscriptionService;
	private final ArticleService articleService;

	public void processMail(String fromName, String fromDomain, String toDomain, String mailingList,
		String htmlLink, String title) {
		UUID userId = userinfoService.findUserinfoByEmail(toDomain).id();
		subscriptionService.addSubscriptionIfUnsubscribed(userId, fromName, fromDomain,
			mailingList);
		articleService.saveArticleBatch(userId, fromName, fromDomain, mailingList, htmlLink, title);

		//TODO: 아티클 저장 후 배치처리 된 애들에 대해서 별도로 event 형식으로 fcm noti 보내는거 추가 (여기 말고)
	}
}
