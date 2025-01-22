package com.newzet.api.user;

import java.util.ArrayList;
import java.util.List;

import com.newzet.api.newsletter.Newsletter;

public class ActiveUser implements User, Activable {

	private List<Newsletter> subscribedNewsletterList = new ArrayList<>();

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
		return false;
	}

	@Override
	public int countSubscription() {
		return 0;
	}
}
