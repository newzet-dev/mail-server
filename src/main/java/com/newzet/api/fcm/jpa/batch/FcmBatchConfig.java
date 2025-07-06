package com.newzet.api.fcm.jpa.batch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.newzet.api.common.batch.config.BatchConfig;
import com.newzet.api.fcm.business.batch.FcmBatchConsumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FcmBatchConfig {

	private final FcmBatchConsumer fcmBatchConsumer;
	private final BatchConfig batchConfig;

	@Bean
	public CommandLineRunner fcmBatchRunner() {
		return args -> {
			if (batchConfig.isAutoStart()) {
				log.info("Auto-starting FCM batch processor");
				fcmBatchConsumer.startProcessing();
			}
		};
	}
}
