package com.newzet.api.auth.repository;

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

import com.newzet.api.auth.business.dto.TokenDTO;
import com.newzet.api.auth.business.service.JwtFactory;
import com.newzet.api.auth.domain.Token;
import com.newzet.api.config.JwtTestConfig;
import com.newzet.api.config.OAuthTestConfig;
import com.newzet.api.config.PostgresTestContainerConfig;
import com.newzet.api.config.RedisTestContainerConfig;

@ExtendWith({RedisTestContainerConfig.class, PostgresTestContainerConfig.class,
	JwtTestConfig.class, OAuthTestConfig.class})
@SpringBootTest
@ComponentScan(basePackages = {"com.newzet.api.auth", "com.newzet.api.common"})
class RedisTokenRepositoryImplIntegrationTest {

	@Autowired
	private RedisTokenRepositoryImpl tokenRepository;

	@Autowired
	private JwtFactory jwtFactory;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private UUID userId;
	private String deviceType;
	private Token refreshToken;
	private TokenDTO refreshTokenDTO;

	@BeforeEach
	void setUp() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();

		userId = UUID.randomUUID();
		deviceType = "mobile";

		refreshToken = jwtFactory.createRefreshToken(userId);
		refreshTokenDTO = refreshToken.toTokenDTO();
	}

	@Test
	public void saveToken_whenValidInputs_thenTokenIsStored() {
		// When
		tokenRepository.saveToken(userId, deviceType, refreshTokenDTO);

		// Then
		TokenDTO foundTokenDTO = tokenRepository.findToken(userId, deviceType);
		Optional<Token> foundToken = jwtFactory.parseToken(foundTokenDTO.value());

		assertThat(foundToken).isPresent();
		assertThat(foundToken.get().getValue()).isEqualTo(refreshToken.getValue());
		assertThat(foundToken.get().getSubject()).isEqualTo(userId.toString());
		assertThat(foundToken.get().isRefreshToken()).isTrue();
	}

	@Test
	public void findToken_whenTokenDoesNotExist_thenReturnsNullTokenValue() {
		// Given
		UUID nonExistingUserId = UUID.randomUUID();

		// When
		TokenDTO result = tokenRepository.findToken(nonExistingUserId, deviceType);

		// Then
		assertThat(result.value()).isNull();
	}

	@Test
	public void removeToken_whenTokenExists_thenTokenIsRemoved() {
		// Given
		tokenRepository.saveToken(userId, deviceType, refreshTokenDTO);
		UUID nonExistingUserId = UUID.randomUUID();

		// When
		tokenRepository.removeToken(userId, deviceType);

		// Then
		TokenDTO result = tokenRepository.findToken(nonExistingUserId, deviceType);
		assertThat(result.value()).isNull();
	}

	@Test
	public void getLastRefreshTime_whenTimeIsSet_thenReturnsCorrectTime() {
		// Given
		tokenRepository.saveToken(userId, deviceType, refreshTokenDTO);
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
		tokenRepository.saveToken(userId, deviceType, refreshTokenDTO);

		sleep(1000);

		Token newRefreshToken = jwtFactory.createRefreshToken(userId);
		TokenDTO newRefreshTokenDTO = newRefreshToken.toTokenDTO();

		// When
		tokenRepository.saveToken(userId, deviceType, newRefreshTokenDTO);

		// Then
		TokenDTO foundTokenDTO = tokenRepository.findToken(userId, deviceType);

		assertThat(foundTokenDTO).isNotNull();
		assertThat(foundTokenDTO.value()).isEqualTo(newRefreshToken.getValue());
		assertThat(foundTokenDTO.value()).isNotEqualTo(refreshToken.getValue());
	}

	@Test
	public void saveToken_whenDifferentDeviceTypes_thenStoresSeparateTokens() {
		// Given
		String otherDeviceType = "desktop";
		Token otherDeviceToken = jwtFactory.createRefreshToken(userId);
		TokenDTO otherDeviceTokenDTO = otherDeviceToken.toTokenDTO();

		// When
		tokenRepository.saveToken(userId, deviceType, refreshTokenDTO);
		tokenRepository.saveToken(userId, otherDeviceType, otherDeviceTokenDTO);

		// Then
		TokenDTO mobileToken = tokenRepository.findToken(userId, deviceType);
		TokenDTO desktopToken = tokenRepository.findToken(userId, otherDeviceType);

		assertThat(mobileToken).isNotNull();
		assertThat(desktopToken).isNotNull();
		assertThat(mobileToken.value()).isEqualTo(refreshToken.getValue());
		assertThat(desktopToken.value()).isEqualTo(otherDeviceToken.getValue());

		tokenRepository.removeToken(userId, deviceType);

		assertThat(tokenRepository.findToken(userId, deviceType)).isNotNull();
		assertThat(tokenRepository.findToken(userId, otherDeviceType)).isNotNull();
	}
}
