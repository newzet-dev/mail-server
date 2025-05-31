package com.newzet.api.common.batch.config;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class BatchConfig {
	private final int batchSize = 100;
	private final boolean autoStart = true;
	private final int timeoutSeconds = 10;
}
