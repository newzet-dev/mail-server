package com.newzet.api.auth.infrastructure;

import java.util.Optional;

import com.newzet.api.auth.domain.Token;

public interface TokenRepository {
	void saveToken(String userId, String deviceType, Token token);

	Optional<Token> findToken(String userId, String deviceType);

	void removeToken(String userId, String deviceType);

	Optional<Long> getLastRefreshTime(String userId, String deviceType);

	void updateLastRefreshTime(String userId, String deviceType);
}
