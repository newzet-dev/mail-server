package com.newzet.api.auth.business.service;

import java.util.Optional;
import java.util.UUID;

import com.newzet.api.auth.domain.Token;

public interface TokenRepository {
	void saveToken(UUID userId, String deviceType, Token token);

	Optional<Token> findToken(UUID userId, String deviceType);

	void removeToken(UUID userId, String deviceType);

	Optional<Long> getLastRefreshTime(UUID userId, String deviceType);

	void updateLastRefreshTime(UUID userId, String deviceType);
}
