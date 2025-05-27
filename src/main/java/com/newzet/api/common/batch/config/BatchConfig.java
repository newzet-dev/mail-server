package com.newzet.api.common.batch.config;

import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class BatchConfig {
	private final int batchSize = 100;
	private final boolean autoStart = true;
	private final int timeoutSeconds = 10;
}
