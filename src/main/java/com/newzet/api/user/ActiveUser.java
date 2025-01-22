package com.newzet.api.user;

import java.util.ArrayList;
import java.util.List;

import com.newzet.api.newsletter.Newsletter;

public class ActiveUser implements User, Subscribable {

	private final List<Newsletter> subscribedNewsletterList = new ArrayList<>();

	@Override
	public void verify() {
		// 활성 상태는 검증이 필요 없음
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
