package com.newzet.api.user;

public class ActiveUser implements User {
	@Override
	public void verify() {
		// 활성 상태는 검증이 필요 없음
	}
}
