package com.newzet.api.auth.business.service.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResponse(
	String id,
	@JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
	public record KakaoAccount(
		String email,
		Profile profile
	) {
		public record Profile(
			String nickname
		) {
		}
	}
}
