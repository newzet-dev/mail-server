package com.newzet.api.article.repository.batch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.newzet.api.article.business.batch.ArticleBatchConsumer;
import com.newzet.api.common.batch.config.BatchConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArticleBatchConfig {

	private final ArticleBatchConsumer batchConsumer;
	private final BatchConfig batchConfig;

	@Bean
	public CommandLineRunner articleBatchRunner() {
		return args -> {
			if (batchConfig.isAutoStart()) {
				log.info("Auto-starting article batch processor");
				batchConsumer.startProcessing();
			}
		};
	}
}
