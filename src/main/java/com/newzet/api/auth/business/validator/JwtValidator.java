package com.newzet.api.auth.domain.validator;

import org.springframework.stereotype.Component;

import com.newzet.api.auth.domain.Token;
import com.newzet.api.auth.exception.AccessTokenExpiredException;
import com.newzet.api.auth.exception.JWTBadRequestException;
import com.newzet.api.auth.exception.JWTConflictException;
import com.newzet.api.auth.exception.RefreshTokenStolenException;
import com.newzet.api.auth.infrastructure.TokenRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenValidator {
	private static final long REFRESH_RATE_LIMIT_MILLISECONDS = 60 * 1000;

	private final TokenRepository tokenRepository;

	public void validateRefreshToken(Token token, String userId, String deviceType) {
		if (!token.isRefreshToken()) {
			throw new JWTBadRequestException("리프레시 토큰이 아닙니다.");
		}

		if (token.isExpired()) {
			throw new AccessTokenExpiredException("리프레시 토큰이 만료되었습니다.");
		}

		String storedTokenValue = tokenRepository.findToken(userId, deviceType)
			.map(Token::getValue)
			.orElseThrow(() -> new JWTBadRequestException("저장된 리프레시 토큰이 없습니다. 재로그인이 필요합니다."));

		if (!storedTokenValue.equals(token.getValue())) {
			tokenRepository.removeToken(userId, deviceType);
			throw new RefreshTokenStolenException("리프레시 토큰이 일치하지 않습니다. 토큰 탈취 가능성.");
		}

		Long lastRefreshTime = tokenRepository.getLastRefreshTime(userId, deviceType);

		if ((System.currentTimeMillis() - lastRefreshTime) < REFRESH_RATE_LIMIT_MILLISECONDS) {
			throw new JWTConflictException("1분 이내에 이미 재발급 요청이 있었습니다.");
		}
	}

	public void validateAccessToken(Token token) {
		if (!token.isAccessToken()) {
			throw new JWTBadRequestException("액세스 토큰이 아닙니다.");
		}

		if (token.isExpired()) {
			throw new AccessTokenExpiredException("액세스 토큰이 만료되었습니다.");
		}
	}
}
