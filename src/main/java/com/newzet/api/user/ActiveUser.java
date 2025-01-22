package com.newzet.api.user;

import java.util.ArrayList;
import java.util.List;

import com.newzet.api.newsletter.Newsletter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ActiveUser implements User, Subscribable {

	private final String email;
	private final List<Newsletter> subscribedNewsletterList;

	public static ActiveUser create(String email) {
		return new ActiveUser(email, new ArrayList<>());
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
