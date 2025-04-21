package com.newzet.api.auth.business.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.newzet.api.auth.business.dto.JwtRefreshRequest;
import com.newzet.api.auth.business.dto.JwtResponse;
import com.newzet.api.auth.business.dto.TokenDTO;
import com.newzet.api.auth.business.validator.JwtValidator;
import com.newzet.api.auth.domain.Token;
import com.newzet.api.auth.exception.TokenBadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
	private final JwtFactory jwtFactory;
	private final TokenRepository tokenRepository;
	private final JwtValidator JWTValidator;

	public JwtResponse refreshAccessToken(JwtRefreshRequest request) {
		String refreshTokenValue = request.refreshToken();
		String deviceType = request.deviceType();

		Token refreshToken = jwtFactory.parseToken(refreshTokenValue)
			.orElseThrow(() -> new TokenBadRequestException("유효하지 않은 토큰입니다."));

		UUID userId = UUID.fromString(refreshToken.getSubject());

		JWTValidator.validateRefreshToken(refreshToken, userId, deviceType);

		Token newAccessToken = jwtFactory.createAccessToken(userId);
		Token newRefreshToken = jwtFactory.createRefreshToken(userId);

		TokenDTO newRefreshTokenDTO = newRefreshToken.toTokenDTO();

		tokenRepository.saveToken(userId, deviceType, newRefreshTokenDTO);
		tokenRepository.updateLastRefreshTime(userId, deviceType);

		return new JwtResponse(newAccessToken.getValue(), newRefreshToken.getValue());
	}

	public void logout(UUID userId, String deviceType) {
		tokenRepository.removeToken(userId, deviceType);
	}
}
