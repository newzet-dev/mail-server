package com.newzet.api.config.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import lombok.Getter;

@Configuration
@Getter
public class StorageConfig {

	// AWS S3 Credentials
	@Value("${cloud.aws.s3.bucket}")
	private String awsBucketName;
	@Value("${cloud.aws.region.static}")
	private String awsRegion;
	@Value("${cloud.aws.credentials.access-key}")
	private String awsAccessKey;
	@Value("${cloud.aws.credentials.secret-key}")
	private String awsSecretKey;

	// Supabase S3-compatible Storage Credentials
	@Value("${cloud.supabase.storage.bucket}")
	private String supabaseBucketName;
	@Value("${cloud.supabase.storage.region}")
	private String supabaseRegion;
	@Value("${cloud.supabase.storage.endpoint-url}")
	private String supabaseEndpointUrl;
	@Value("${cloud.supabase.storage.credentials.access-key}")
	private String supabaseAccessKey;
	@Value("${cloud.supabase.storage.credentials.secret-key}")
	private String supabaseSecretKey;

	@Bean
	@Qualifier("awsS3Client")
	public AmazonS3 awsS3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
		return AmazonS3ClientBuilder.standard()
			.withRegion(awsRegion)
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.build();
	}

	@Bean
	@Qualifier("supabaseS3Client")
	public AmazonS3 supabaseS3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(supabaseAccessKey, supabaseSecretKey);
		AwsClientBuilder.EndpointConfiguration endpointConfig =
			new AwsClientBuilder.EndpointConfiguration(supabaseEndpointUrl, supabaseRegion);

		return AmazonS3ClientBuilder.standard()
			.withEndpointConfiguration(endpointConfig)
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.build();
	}
}
