package com.newzet.api.common.batch.config;

import lombok.Getter;

@Getter
public class BatchConfig {
	private final int batchSize = 100;
	private final boolean autoStart = true;
	private final int timeoutSeconds = 10;
}
