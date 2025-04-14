package com.newzet.api.auth.infrastructure;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.newzet.api.auth.business.service.JwtFactory;
import com.newzet.api.auth.domain.Token;
import com.newzet.api.auth.domain.TokenType;

@ExtendWith(MockitoExtension.class)
class RedisRefreshTokenRepositoryTest {

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@Mock
	private JwtFactory jwtFactory;

	private RedisRefreshTokenRepository tokenRepository;
	private String userId;
	private String deviceType;

	@BeforeEach
	void setUp() {
		lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		tokenRepository = new RedisRefreshTokenRepository(redisTemplate, jwtFactory);
		userId = "user123";
		deviceType = "web";
	}

	@Test
	public void saveToken_storesTokenInRedis() {
		//Given
		Token token = Token.of(TokenType.REFRESH, "token-value", userId, new Date(), new Date());
		String expectedKey = "refreshToken:" + userId + ":" + deviceType;

		//When
		tokenRepository.saveToken(userId, deviceType, token);

		//Then
		verify(valueOperations).set(eq(expectedKey), eq(token.getValue()), anyLong(),
			eq(TimeUnit.MILLISECONDS));
		verify(valueOperations).set(startsWith("refresh-time:"), anyString(), anyLong(),
			eq(TimeUnit.MILLISECONDS));
	}

	@Test
	public void findToken_whenTokenExists_returnToken() {
		//Given
		String tokenValue = "token-value";
		Token token = Token.of(TokenType.REFRESH, tokenValue, userId, new Date(), new Date());
		String expectedKey = "refreshToken:" + userId + ":" + deviceType;

		when(valueOperations.get(expectedKey)).thenReturn(tokenValue);
		when(jwtFactory.parseToken(tokenValue)).thenReturn(Optional.of(token));

		//When
		Optional<Token> result = tokenRepository.findToken(userId, deviceType);

		//Then
		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(token);
	}

	@Test
	public void findToken_whenTokenDoesNotExist_returnEmpty() {
		//Given
		String expectedKey = "refreshToken:" + userId + ":" + deviceType;
		when(valueOperations.get(expectedKey)).thenReturn(null);

		//When
		Optional<Token> result = tokenRepository.findToken(userId, deviceType);

		//Then
		assertThat(result).isEmpty();
	}

	@Test
	public void removeToken_deletesTokenFromRedis() {
		//Given
		String expectedKey = "refreshToken:" + userId + ":" + deviceType;

		//When
		tokenRepository.removeToken(userId, deviceType);

		//Then
		verify(redisTemplate).delete(expectedKey);
	}

	@Test
	public void getLastRefreshTime_returnsTime() {
		// Given
		String timeKey = "refresh-time:" + userId + ":" + deviceType;
		long timestamp = System.currentTimeMillis();
		when(valueOperations.get(timeKey)).thenReturn(String.valueOf(timestamp));

		// When
		Optional<Long> result = tokenRepository.getLastRefreshTime(userId, deviceType);

		// Then
		assertThat(result).isPresent();
		assertThat(result.get()).isEqualTo(timestamp);
	}

	@Test
	public void updateLastRefreshTime_storesTimeInRedis() {
		//Given
		String timeKey = "refresh-time:" + userId + ":" + deviceType;

		//When
		tokenRepository.updateLastRefreshTime(userId, deviceType);

		//Then
		verify(valueOperations).set(eq(timeKey), anyString(), anyLong(), eq(TimeUnit.MILLISECONDS));
	}

	@Test
	void getLastRefreshTime_shouldReturnEmptyOptional_whenTimeDoesNotExist() {
		// given
		String timeKey = "refresh-time:" + userId + ":" + deviceType;
		when(redisTemplate.opsForValue().get(timeKey)).thenReturn(null);

		// when
		Optional<Long> result = tokenRepository.getLastRefreshTime(userId, deviceType);

		// then
		assertThat(result).isEmpty();
	}
}
