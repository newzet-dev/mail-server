package com.newzet.api.common.auth.business;

import org.springframework.stereotype.Component;

import com.newzet.api.common.auth.domain.Token;
import com.newzet.api.common.auth.exception.TokenExpiredException;

@Component
public class TokenValidator {
	public void validate(Token token) {
		if (token.isExpired()) {
			throw new TokenExpiredException("액세스 토큰이 만료되었습니다.");
		}
	}
}
