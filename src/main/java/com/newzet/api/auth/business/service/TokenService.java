package com.newzet.api.auth.business.service;

import org.springframework.stereotype.Service;

import com.newzet.api.auth.business.dto.JWTResponseDTO;
import com.newzet.api.auth.business.dto.TokenRefreshRequestDTO;
import com.newzet.api.auth.domain.Token;
import com.newzet.api.auth.domain.validator.TokenValidator;
import com.newzet.api.auth.exception.JWTBadRequestException;
import com.newzet.api.auth.infrastructure.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
	private final TokenFactory tokenFactory;
	private final TokenRepository tokenRepository;
	private final TokenValidator tokenValidator;

	public JWTResponseDTO refreshAccessToken(TokenRefreshRequestDTO request) {
		String refreshTokenValue = request.refreshToken();
		String deviceType = request.deviceType();

		Token refreshToken = tokenFactory.parseToken(refreshTokenValue)
			.orElseThrow(() -> new JWTBadRequestException("유효하지 않은 토큰입니다."));

		String userId = refreshToken.getSubject();

		tokenValidator.validateRefreshToken(refreshToken, userId, deviceType);

		Token newAccessToken = tokenFactory.createAccessToken(userId);
		Token newRefreshToken = tokenFactory.createRefreshToken(userId);

		tokenRepository.saveToken(userId, deviceType, newRefreshToken);
		tokenRepository.updateLastRefreshTime(userId, deviceType);

		return JWTResponseDTO.builder()
			.accessToken(newAccessToken.getValue())
			.refreshToken(newRefreshToken.getValue())
			.build();
	}

	public void logout(String userId, String deviceType) {
		tokenRepository.removeToken(userId, deviceType);
	}
}
