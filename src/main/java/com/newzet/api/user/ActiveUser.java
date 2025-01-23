package com.newzet.api.user;

import java.util.List;

import com.newzet.api.newsletter.Newsletter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ActiveUser implements User, Subscribable {

	private final String email;
	private final List<Newsletter> subscribedNewsletterList;

	public static ActiveUser create(String email, List<Newsletter> subscribedNewsletterList) {
		return new ActiveUser(email, subscribedNewsletterList);
	}

	@Override
	public void subscribe(Newsletter newsletter) {
		if (!subscribedNewsletterList.contains(newsletter)) {
			subscribedNewsletterList.add(newsletter);
		}
	}

	@Override
	public boolean isSubscribed(Newsletter newsletter) {
		return subscribedNewsletterList.contains(newsletter);
	}

	@Override
	public int countSubscription() {
		return subscribedNewsletterList.size();
	}

}
