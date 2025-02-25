package com.newzet.api.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class TestRedisConfig implements BeforeAllCallback {
	private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
	private static final int REDIS_PORT = 6379;
	private GenericContainer redisGenericContainer;

	@Override
	public void beforeAll(ExtensionContext context) {
		redisGenericContainer = new GenericContainer(
			DockerImageName.parse(REDIS_IMAGE)).withExposedPorts(REDIS_PORT);

		redisGenericContainer.start();

		System.setProperty("spring.data.redis.host", redisGenericContainer.getHost());
		System.setProperty("spring.data.redis.port",
			String.valueOf(redisGenericContainer.getMappedPort(REDIS_PORT)));
	}
}
