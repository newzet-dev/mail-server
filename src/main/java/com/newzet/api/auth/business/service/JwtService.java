package com.newzet.api.auth.business.service;

import org.springframework.stereotype.Service;

import com.newzet.api.auth.business.dto.JwtRefreshRequestDTO;
import com.newzet.api.auth.business.dto.JwtResponseDTO;
import com.newzet.api.auth.business.validator.JwtValidator;
import com.newzet.api.auth.domain.Token;
import com.newzet.api.auth.exception.TokenBadRequestException;
import com.newzet.api.auth.infrastructure.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
	private final JwtFactory jwtFactory;
	private final TokenRepository tokenRepository;
	private final JwtValidator JWTValidator;

	public JwtResponseDTO refreshAccessToken(JwtRefreshRequestDTO request) {
		String refreshTokenValue = request.refreshToken();
		String deviceType = request.deviceType();

		Token refreshToken = jwtFactory.parseToken(refreshTokenValue)
			.orElseThrow(() -> new TokenBadRequestException("유효하지 않은 토큰입니다."));

		String userId = refreshToken.getSubject();

		JWTValidator.validateRefreshToken(refreshToken, userId, deviceType);

		Token newAccessToken = jwtFactory.createAccessToken(userId);
		Token newRefreshToken = jwtFactory.createRefreshToken(userId);

		tokenRepository.saveToken(userId, deviceType, newRefreshToken);
		tokenRepository.updateLastRefreshTime(userId, deviceType);

		return new JwtResponseDTO(newAccessToken.getValue(), newRefreshToken.getValue());
	}

	public void logout(String userId, String deviceType) {
		tokenRepository.removeToken(userId, deviceType);
	}
}
