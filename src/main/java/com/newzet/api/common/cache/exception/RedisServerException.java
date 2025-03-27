package com.newzet.api.common.cache.exception;

import org.springframework.data.redis.connection.RedisServer;

public class RedisServerException extends RuntimeException {
	public RedisServerException() {
		super();
	}
}
