package com.newzet.api.config.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.newzet.api.common.cache.local.CacheEntry;

@Configuration
public class CaffeineConfig {
	@Bean
	public Cache<String, CacheEntry<?>> caffeineCache() {
		return Caffeine.newBuilder()
			.maximumSize(10000)
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.recordStats()
			.build();
	}
}
