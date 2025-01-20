package com.newzet.api.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ActiveUserTest {

	@Test
	@DisplayName("활성 유저는 검증 시 아무것도 일어나지 않음")
	void verify() {
		//given
		User user = new ActiveUser();

		//when
		user.verify();
	}
}
