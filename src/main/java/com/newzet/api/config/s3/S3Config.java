package com.newzet.api.config.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class S3Config {

	@Value("s3.content-bucket")
	private String contentBucketName;

}
