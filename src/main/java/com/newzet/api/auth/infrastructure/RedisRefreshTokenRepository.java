package com.newzet.api.auth.infrastructure;

import static com.newzet.api.auth.business.service.JwtFactory.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.newzet.api.auth.business.service.JwtFactory;
import com.newzet.api.auth.domain.Token;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements TokenRepository {
	private static final String TOKEN_KEY_PREFIX = "refreshToken:";
	private static final String REFRESH_TIME_KEY_PREFIX = "refresh-time:";
	private static final long REFRESH_REQUEST_INTERVAL_LIMIT = 60 * 1000L;

	private final RedisTemplate<String, String> redisTemplate;
	private final JwtFactory jwtFactory;

	@Override
	public void saveToken(String userId, String deviceType, Token token) {
		String key = generateKey(userId, deviceType);
		redisTemplate.opsForValue()
			.set(key, token.getValue(), REFRESH_TOKEN_VALIDITY_MILLISECONDS, TimeUnit.MILLISECONDS);
		updateLastRefreshTime(userId, deviceType);
	}

	@Override
	public Optional<Token> findToken(String userId, String deviceType) {
		String key = generateKey(userId, deviceType);
		String tokenValue = redisTemplate.opsForValue().get(key);
		return jwtFactory.parseToken(tokenValue);
	}

	@Override
	public void removeToken(String userId, String deviceType) {
		String key = generateKey(userId, deviceType);
		redisTemplate.delete(key);
	}

	@Override
	public Optional<Long> getLastRefreshTime(String userId, String deviceType) {
		String timeKey = generateTimeKey(userId, deviceType);
		String value = redisTemplate.opsForValue().get(timeKey);
		return Optional.ofNullable(value).map(Long::valueOf);
	}

	@Override
	public void updateLastRefreshTime(String userId, String deviceType) {
		String timeKey = generateTimeKey(userId, deviceType);
		redisTemplate.opsForValue()
			.set(timeKey, String.valueOf(System.currentTimeMillis()),
				REFRESH_REQUEST_INTERVAL_LIMIT, TimeUnit.MILLISECONDS);
	}

	private String generateKey(String userId, String deviceType) {
		return TOKEN_KEY_PREFIX + userId + ":" + deviceType;
	}

	private String generateTimeKey(String userId, String deviceType) {
		return REFRESH_TIME_KEY_PREFIX + userId + ":" + deviceType;
	}
}
