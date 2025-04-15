package com.newzet.api.auth.business.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.auth.business.dto.JwtRefreshRequest;
import com.newzet.api.auth.business.dto.JwtResponse;
import com.newzet.api.auth.business.validator.JwtValidator;
import com.newzet.api.auth.domain.Token;
import com.newzet.api.auth.domain.TokenType;
import com.newzet.api.auth.exception.TokenBadRequestException;
import com.newzet.api.auth.infrastructure.TokenRepository;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

	@Mock
	private JwtFactory jwtFactory;

	@Mock
	private TokenRepository tokenRepository;

	@Mock
	private JwtValidator JWTValidator;

	private JwtService jwtService;
	private UUID userId;
	private String deviceType;

	@BeforeEach
	void setUp() {
		jwtService = new JwtService(jwtFactory, tokenRepository, JWTValidator);
		userId = UUID.randomUUID();
		deviceType = "web";
	}

	@Test
	public void refreshAccessToken_whenValidRequest_returnNewTokens() {
		//Given
		String refreshTokenValue = "refresh-token-value";
		JwtRefreshRequest request = new JwtRefreshRequest(refreshTokenValue, deviceType);

		Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
		Token refreshToken = Token.of(TokenType.REFRESH, refreshTokenValue, userId.toString(),
			new Date(),
			future);
		Token newAccessToken = Token.of(TokenType.ACCESS, "new-access-token", userId.toString(),
			new Date(),
			future);
		Token newRefreshToken = Token.of(TokenType.REFRESH, "new-refresh-token", userId.toString(),
			new Date(),
			future);

		when(jwtFactory.parseToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
		when(jwtFactory.createAccessToken(userId)).thenReturn(newAccessToken);
		when(jwtFactory.createRefreshToken(userId)).thenReturn(newRefreshToken);
		doNothing().when(JWTValidator).validateRefreshToken(any(), eq(userId), eq(deviceType));

		//When
		JwtResponse response = jwtService.refreshAccessToken(request);

		//Then
		assertThat(response.accessToken()).isEqualTo(newAccessToken.getValue());
		assertThat(response.refreshToken()).isEqualTo(newRefreshToken.getValue());
		verify(tokenRepository).saveToken(userId, deviceType, newRefreshToken);
		verify(tokenRepository).updateLastRefreshTime(userId, deviceType);
	}

	@Test
	public void refreshAccessToken_whenInvalidToken_throwException() {
		//Given
		String refreshTokenValue = "invalid-token";
		JwtRefreshRequest request = new JwtRefreshRequest(refreshTokenValue, deviceType);

		when(jwtFactory.parseToken(refreshTokenValue)).thenReturn(Optional.empty());

		//When, Then
		assertThatThrownBy(() -> jwtService.refreshAccessToken(request))
			.isInstanceOf(TokenBadRequestException.class);
	}

	@Test
	public void logout_callsRemoveToken() {
		//When
		jwtService.logout(userId, deviceType);

		//Then
		verify(tokenRepository).removeToken(userId, deviceType);
	}

	@Test
	public void refreshAccessToken_whenValidatorThrowsException_propagatesException() {
		//Given
		String refreshTokenValue = "refresh-token-value";
		JwtRefreshRequest request = new JwtRefreshRequest(refreshTokenValue, deviceType);

		Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
		Token refreshToken = Token.of(TokenType.REFRESH, refreshTokenValue, userId.toString(),
			new Date(),
			future);

		when(jwtFactory.parseToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
		doThrow(new TokenBadRequestException("Invalid token"))
			.when(JWTValidator).validateRefreshToken(any(), eq(userId), eq(deviceType));

		//When, Then
		assertThatThrownBy(() -> jwtService.refreshAccessToken(request))
			.isInstanceOf(TokenBadRequestException.class);
	}

	@Test
	public void refreshAccessToken_whenAccessTokenCreationFails_throwsException() {
		//Given
		String refreshTokenValue = "refresh-token-value";
		JwtRefreshRequest request = new JwtRefreshRequest(refreshTokenValue, deviceType);

		Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
		Token refreshToken = Token.of(TokenType.REFRESH, refreshTokenValue, userId.toString(),
			new Date(),
			future);

		when(jwtFactory.parseToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
		doNothing().when(JWTValidator).validateRefreshToken(any(), eq(userId), eq(deviceType));
		when(jwtFactory.createAccessToken(userId)).thenThrow(
			new RuntimeException("Token creation failed"));

		//When, Then
		assertThatThrownBy(() -> jwtService.refreshAccessToken(request))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Token creation failed");
	}

	@Test
	public void refreshAccessToken_whenRefreshTokenCreationFails_throwsException() {
		//Given
		String refreshTokenValue = "refresh-token-value";
		JwtRefreshRequest request = new JwtRefreshRequest(refreshTokenValue, deviceType);

		Date future = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
		Token refreshToken = Token.of(TokenType.REFRESH, refreshTokenValue, userId.toString(),
			new Date(),
			future);
		Token accessToken = Token.of(TokenType.ACCESS, "access-token", userId.toString(),
			new Date(), future);

		when(jwtFactory.parseToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
		doNothing().when(JWTValidator).validateRefreshToken(any(), eq(userId), eq(deviceType));
		when(jwtFactory.createAccessToken(userId)).thenReturn(accessToken);
		when(jwtFactory.createRefreshToken(userId)).thenThrow(
			new RuntimeException("Token creation failed"));

		//When, Then
		assertThatThrownBy(() -> jwtService.refreshAccessToken(request))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Token creation failed");
	}
}
