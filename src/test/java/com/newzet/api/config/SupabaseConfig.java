package com.newzet.api.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class SupabaseConfig implements BeforeAllCallback {

	public static final String TEST_SUPABASE_URL = "aaaaaaaaaa";
	public static final String TEST_SUPABASE_KEY = "aaaaaaaaaaaaaa";

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		System.setProperty("supabase.url", TEST_SUPABASE_URL);
		System.setProperty("supabase.service-key", TEST_SUPABASE_KEY);
	}
}
