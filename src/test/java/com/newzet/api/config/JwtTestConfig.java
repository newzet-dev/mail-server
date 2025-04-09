package com.newzet.api.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class JwtTestConfig implements BeforeAllCallback {

	private static final String TEST_JWT_SECRET = "testSecretKeyForJwtTestingPurposesOnlyDoNotUseInProduction";

	@Override
	public void beforeAll(ExtensionContext context) {
		System.setProperty("jwt.secret", TEST_JWT_SECRET);
	}
}
