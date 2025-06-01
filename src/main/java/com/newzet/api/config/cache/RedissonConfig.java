package com.newzet.api.config.cache;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

	private static final String REDISSON_HOST_PREFIX = "redis://";
	@Value("${spring.data.redis.host}")
	private String redisHost;
	@Value("${spring.data.redis.port}")
	private int redisPort;
	@Value("${spring.data.redis.password}")
	private String password;

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort)
			.setPassword(password)
			.setDnsMonitoringInterval(30000)
			.setConnectTimeout(1000)
			.setTimeout(500)
			.setRetryAttempts(1)
			.setRetryInterval(200)
			.setPingConnectionInterval(30000);

		return Redisson.create(config);
	}
}
