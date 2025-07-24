package com.newzet.api.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class S3TestConfig implements BeforeAllCallback {

	private static final String S3_REGION = "ap-northeast-2";
	private static final String S3_ACCESS_KEY= "Aaaaaaaa";
	private static final String	S3_SECRET_KEY = "Aaaaaaaa";
	private static final String S3_CONTENT_BUCKET_NAME="aaaaaa";

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		System.setProperty("spring.cloud.aws.region.static", S3_REGION);
		System.setProperty("spring.cloud.aws.credentials.access-key", S3_ACCESS_KEY);
		System.setProperty("spring.cloud.aws.credentials.secret-key", S3_SECRET_KEY);
		System.setProperty("s3.content-bucket",  S3_CONTENT_BUCKET_NAME);
	}
}
