package com.newzet.api.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.newzet.api.newsletter.Newsletter;
import com.newzet.api.newsletter.RegisteredNewsletter;

class ActiveUserTest {

	@Test
	@DisplayName("활성 유저는 검증 시 아무것도 일어나지 않음")
	void verify() {
		//given
		User user = new ActiveUser();

		//when
		user.verify();
	}

	@Test
	@DisplayName("처음 구독하는 뉴스레터는 구독 목록에 추가됨")
	public void 구독하지_않은_뉴스레터_구독() {
		//given
		ActiveUser activeUser = new ActiveUser();
		activeUser.subscribe(new RegisteredNewsletter());
		int beforeSize = activeUser.getSubscribedNewsletterList().size();

		//when
		activeUser.subscribe(new RegisteredNewsletter());

		//then
		assertEquals(beforeSize + 1, activeUser.getSubscribedNewsletterList().size());
	}

	@Test
	@DisplayName("이미 구독한 뉴스레터는 추가되지 않음")
	public void 이미_구독한_뉴스레터_구독() {
		//given
		ActiveUser activeUser = new ActiveUser();
		Newsletter newsletter = new RegisteredNewsletter();
		activeUser.subscribe(newsletter);
		int beforeSize = activeUser.getSubscribedNewsletterList().size();

		//when
		activeUser.subscribe(newsletter);

		//then
		assertEquals(beforeSize, activeUser.getSubscribedNewsletterList().size());
	}
}
