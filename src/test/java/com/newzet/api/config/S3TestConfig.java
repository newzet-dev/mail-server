package com.newzet.api.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class S3TestConfig implements BeforeAllCallback {

	// AWS S3 Test Properties
	private static final String S3_REGION = "ap-northeast-2";
	private static final String S3_ACCESS_KEY= "Aaaaaaaa";
	private static final String	S3_SECRET_KEY = "Aaaaaaaa";
	private static final String S3_CONTENT_BUCKET_NAME="aaaaaa";

	// Supabase Test Properties
	private static final String SUPABASE_BUCKET_NAME = "supabase-test-bucket";
	private static final String SUPABASE_ENDPOINT_URL = "https://example.supabase.co";
	private static final String SUPABASE_REGION = "ap-northeast-2";
	private static final String SUPABASE_ACCESS_KEY = "supabase_test_access_key";
	private static final String SUPABASE_SECRET_KEY = "supabase_test_secret_key";

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		// Set AWS Properties
		System.setProperty("cloud.aws.region.static", S3_REGION);
		System.setProperty("cloud.aws.credentials.access-key", S3_ACCESS_KEY);
		System.setProperty("cloud.aws.credentials.secret-key", S3_SECRET_KEY);
		System.setProperty("cloud.aws.s3.bucket",  S3_CONTENT_BUCKET_NAME);

		// Set Supabase Properties
		System.setProperty("cloud.supabase.storage.bucket", SUPABASE_BUCKET_NAME);
		System.setProperty("cloud.supabase.storage.endpoint-url", SUPABASE_ENDPOINT_URL);
		System.setProperty("cloud.supabase.storage.region", SUPABASE_REGION);
		System.setProperty("cloud.supabase.storage.credentials.access-key", SUPABASE_ACCESS_KEY);
		System.setProperty("cloud.supabase.storage.credentials.secret-key", SUPABASE_SECRET_KEY);
	}
}
