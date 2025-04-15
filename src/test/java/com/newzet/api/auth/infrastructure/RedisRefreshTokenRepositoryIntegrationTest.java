package com.newzet.api.auth.infrastructure;

import static jodd.util.ThreadUtil.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;

import com.newzet.api.auth.business.service.JwtFactory;
import com.newzet.api.auth.domain.Token;
import com.newzet.api.config.JwtTestConfig;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.config.RedisTestContainerConfig;

@ExtendWith({RedisTestContainerConfig.class, PostgresTestContainerConfig.class,
	JwtTestConfig.class})
@SpringBootTest
@ComponentScan(basePackages = {"com.newzet.api.auth", "com.newzet.api.common"})
class RedisRefreshTokenRepositoryIntegrationTest {

	@Autowired
	private RedisRefreshTokenRepository tokenRepository;

	@Autowired
	private JwtFactory jwtFactory;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private UUID userId;
	private String deviceType;
	private Token refreshToken;

	@BeforeEach
	void setUp() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();

		userId = UUID.randomUUID();
		deviceType = "mobile";

		refreshToken = jwtFactory.createRefreshToken(userId);
	}

	@Test
	public void saveToken_whenValidInputs_thenTokenIsStored() {
		// When
		tokenRepository.saveToken(userId, deviceType, refreshToken);

		// Then
		Optional<Token> foundToken = tokenRepository.findToken(userId, deviceType);

		assertThat(foundToken).isPresent();
		assertThat(foundToken.get().getValue()).isEqualTo(refreshToken.getValue());
		assertThat(foundToken.get().getSubject()).isEqualTo(userId.toString());
		assertThat(foundToken.get().isRefreshToken()).isTrue();
	}

	@Test
	public void findToken_whenTokenDoesNotExist_thenReturnsEmpty() {
		// Given
		UUID nonExistingUserId = UUID.randomUUID();

		// When
		Optional<Token> result = tokenRepository.findToken(nonExistingUserId, deviceType);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	public void removeToken_whenTokenExists_thenTokenIsRemoved() {
		// Given
		tokenRepository.saveToken(userId, deviceType, refreshToken);

		// When
		tokenRepository.removeToken(userId, deviceType);

		// Then
		Optional<Token> result = tokenRepository.findToken(userId, deviceType);
		assertThat(result).isEmpty();
	}

	@Test
	public void getLastRefreshTime_whenTimeIsSet_thenReturnsCorrectTime() {
		// Given
		tokenRepository.saveToken(userId, deviceType, refreshToken);
		long beforeUpdate = System.currentTimeMillis();

		// When
		tokenRepository.updateLastRefreshTime(userId, deviceType);
		long afterUpdate = System.currentTimeMillis();
		Optional<Long> lastRefreshTime = tokenRepository.getLastRefreshTime(userId, deviceType);

		// Then
		assertThat(lastRefreshTime).isPresent();
		assertThat(lastRefreshTime.get()).isBetween(beforeUpdate, afterUpdate);
	}

	@Test
	public void getLastRefreshTime_whenTimeNotSet_thenReturnsEmpty() {
		// Given
		UUID newUserId = UUID.randomUUID();

		// When
		Optional<Long> lastRefreshTime = tokenRepository.getLastRefreshTime(newUserId, deviceType);

		// Then
		assertThat(lastRefreshTime).isEmpty();
	}

	@Test
	public void saveToken_whenCalledMultipleTimesForSameUser_thenOverwritesPreviousToken() {
		// Given
		tokenRepository.saveToken(userId, deviceType, refreshToken);

		sleep(1000);

		Token newRefreshToken = jwtFactory.createRefreshToken(userId);

		// When
		tokenRepository.saveToken(userId, deviceType, newRefreshToken);

		// Then
		Optional<Token> foundToken = tokenRepository.findToken(userId, deviceType);

		assertThat(foundToken).isPresent();
		assertThat(foundToken.get().getValue()).isEqualTo(newRefreshToken.getValue());
		assertThat(foundToken.get().getValue()).isNotEqualTo(refreshToken.getValue());
	}

	@Test
	public void saveToken_whenDifferentDeviceTypes_thenStoresSeparateTokens() {
		// Given
		String otherDeviceType = "desktop";
		Token otherDeviceToken = jwtFactory.createRefreshToken(userId);

		// When
		tokenRepository.saveToken(userId, deviceType, refreshToken);
		tokenRepository.saveToken(userId, otherDeviceType, otherDeviceToken);

		// Then
		Optional<Token> mobileToken = tokenRepository.findToken(userId, deviceType);
		Optional<Token> desktopToken = tokenRepository.findToken(userId, otherDeviceType);

		assertThat(mobileToken).isPresent();
		assertThat(desktopToken).isPresent();
		assertThat(mobileToken.get().getValue()).isEqualTo(refreshToken.getValue());
		assertThat(desktopToken.get().getValue()).isEqualTo(otherDeviceToken.getValue());

		tokenRepository.removeToken(userId, deviceType);

		assertThat(tokenRepository.findToken(userId, deviceType)).isEmpty();
		assertThat(tokenRepository.findToken(userId, otherDeviceType)).isPresent();
	}
}
