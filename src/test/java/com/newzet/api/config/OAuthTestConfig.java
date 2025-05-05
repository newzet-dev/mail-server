package com.newzet.api.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class OAuthTestConfig implements BeforeAllCallback {

	private static final String TEST_WEB_REDIRECT_URI = "http://test";
	private static final String TEST_APP_REDIRECT_URI = "newzet://test";
	private static final String TEST_KAKAO_CLIENT_ID = "testclientidtestclientid";
	private static final String TEST_KAKAO_BACKEND_REDIRECT_URI = "http://test/auth/oauth/kakao/callback";

	@Override
	public void beforeAll(ExtensionContext context) {
		System.setProperty("OAUTH_WEB-REDIRECT-URI", TEST_WEB_REDIRECT_URI);
		System.setProperty("OAUTH_APP-REDIRECT-URI", TEST_APP_REDIRECT_URI);
		System.setProperty("OAUTH_KAKAO_CLIENT-ID", TEST_KAKAO_CLIENT_ID);
		System.setProperty("OAUTH_KAKAO_BACKEND-REDIRECT-URI", TEST_KAKAO_BACKEND_REDIRECT_URI);
	}
}
