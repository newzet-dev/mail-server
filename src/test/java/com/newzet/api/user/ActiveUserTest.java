package com.newzet.api.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.newzet.api.newsletter.domain.Newsletter;
import com.newzet.api.newsletter.domain.RegisteredNewsletter;
import com.newzet.api.user.domain.ActiveUser;

class ActiveUserTest {
	@Test
	@DisplayName("처음 구독하는 뉴스레터는 구독 목록에 추가됨")
	public void 구독하지_않은_뉴스레터_구독() {
		//given
		ActiveUser activeUser = ActiveUser.create("test@email.com", new ArrayList<>());
		int numberOfSubscription = activeUser.countSubscription();

		//when
		Newsletter newsletter = new RegisteredNewsletter();
		activeUser.subscribe(newsletter);

		//then
		assertTrue(activeUser.isSubscribed(newsletter));
		assertEquals(activeUser.countSubscription(), numberOfSubscription + 1);
	}

	@Test
	@DisplayName("이미 구독한 뉴스레터는 추가되지 않음")
	public void 이미_구독한_뉴스레터_구독() {
		//given
		ActiveUser activeUser = ActiveUser.create("test@email.com", new ArrayList<>());
		Newsletter newsletter = new RegisteredNewsletter();
		activeUser.subscribe(newsletter);
		int numberOfSubscription = activeUser.countSubscription();

		//when
		activeUser.subscribe(newsletter);

		//then
		assertTrue(activeUser.isSubscribed(newsletter));
		assertEquals(activeUser.countSubscription(), numberOfSubscription);
	}
}
