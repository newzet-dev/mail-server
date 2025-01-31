package com.newzet.api.newsletter.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

@TestConfiguration
public class EmbeddedRedisConfig {

	@Value("${spring.redis.port}")
	private int port;

	private RedisServer redisServer;

	@PostConstruct
	public void startRedis() {
		try {
			System.out.println("hello");
			redisServer = new RedisServer(port);
			this.redisServer.start();
			System.out.println("✅ Embedded Redis Started on port: " + port);
		} catch (IOException e) {
			System.out.println("hi");
		}

	}

	@PreDestroy
	public void stopRedis() throws IOException {
		this.redisServer.stop();

		System.out.println("🛑 Embedded Redis Stopped");
	}

}
