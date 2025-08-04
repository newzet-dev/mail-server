package com.newzet.api.config.cache;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Value("${spring.data.redis.password}")
	private String password;

	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(host, port);
		serverConfig.setPassword(RedisPassword.of(password));

		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
			.commandTimeout(Duration.ofMillis(1000))
			.clientOptions(ClientOptions.builder()
				.autoReconnect(true)
				.socketOptions(
					SocketOptions.builder()
						.connectTimeout(Duration.ofMillis(1000))
						.keepAlive(true)
						.tcpNoDelay(true)
						.build())
				.disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
				.build())
			.clientResources(clientResources())
			.build();

		return new LettuceConnectionFactory(serverConfig, clientConfig);
	}

	@Bean(destroyMethod = "shutdown")
	public ClientResources clientResources() {
		return DefaultClientResources.builder()
			.ioThreadPoolSize(8)
			.computationThreadPoolSize(8)
			.build();
	}

	@Bean
	public RedisTemplate<?, ?> redisTemplate() {
		RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		return redisTemplate;
	}

	@Bean
	public ReactiveRedisTemplate<String, String> reactiveRedisTemplate() {
		LettuceConnectionFactory connectionFactory = redisConnectionFactory();

		StringRedisSerializer keySerializer = new StringRedisSerializer();
		StringRedisSerializer valueSerializer = new StringRedisSerializer();

		RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
			.<String, String>newSerializationContext()
			.key(keySerializer)
			.value(valueSerializer)
			.hashKey(keySerializer)
			.hashValue(valueSerializer)
			.build();

		return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
	}
}
