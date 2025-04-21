package com.newzet.api.auth.repository;

import static com.newzet.api.auth.business.service.JwtFactory.*;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.newzet.api.auth.business.dto.TokenDTO;
import com.newzet.api.auth.business.service.TokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisTokenRepositoryImpl implements TokenRepository {
	private static final String TOKEN_KEY_PREFIX = "refreshToken:";
	private static final String REFRESH_TIME_KEY_PREFIX = "refresh-time:";
	private static final long REFRESH_REQUEST_INTERVAL_LIMIT = 60 * 1000L;

	private final RedisTemplate<String, String> redisTemplate;

	@Override
	public void saveToken(UUID userId, String deviceType, TokenDTO tokenDTO) {
		String key = generateKey(userId, deviceType);
		redisTemplate.opsForValue()
			.set(key, tokenDTO.value(), REFRESH_TOKEN_VALIDITY_MILLISECONDS, TimeUnit.MILLISECONDS);
		updateLastRefreshTime(userId, deviceType);
	}

	@Override
	public TokenDTO findToken(UUID userId, String deviceType) {
		String key = generateKey(userId, deviceType);
		String tokenValue = redisTemplate.opsForValue().get(key);
		return TokenDTO.from(tokenValue);
	}

	@Override
	public void removeToken(UUID userId, String deviceType) {
		String key = generateKey(userId, deviceType);
		redisTemplate.delete(key);
	}

	@Override
	public Optional<Long> getLastRefreshTime(UUID userId, String deviceType) {
		String timeKey = generateTimeKey(userId, deviceType);
		String value = redisTemplate.opsForValue().get(timeKey);
		return Optional.ofNullable(value).map(Long::valueOf);
	}

	@Override
	public void updateLastRefreshTime(UUID userId, String deviceType) {
		String timeKey = generateTimeKey(userId, deviceType);
		redisTemplate.opsForValue()
			.set(timeKey, String.valueOf(System.currentTimeMillis()),
				REFRESH_REQUEST_INTERVAL_LIMIT, TimeUnit.MILLISECONDS);
	}

	private String generateKey(UUID userId, String deviceType) {
		return TOKEN_KEY_PREFIX + userId + ":" + deviceType;
	}

	private String generateTimeKey(UUID userId, String deviceType) {
		return REFRESH_TIME_KEY_PREFIX + userId + ":" + deviceType;
	}
}
