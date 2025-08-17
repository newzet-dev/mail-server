package com.newzet.api.common.auth.business;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.newzet.api.common.auth.domain.Token;
import com.newzet.api.common.auth.exception.TokenBadRequestException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenConverter {
	private final SecretKey secretKey;

	public TokenConverter(@Value("${jwt.secret}") String secret) {
		byte[] keyBytes = Base64.getDecoder().decode(secret);
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public Token toToken(String tokenValue) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(tokenValue)
				.getPayload();

			String subject = claims.getSubject();
			Date issuedAt = claims.getIssuedAt();
			Date expiredAt = claims.getExpiration();

			return Token.of(subject, issuedAt, expiredAt);
		} catch (Exception e) {
			throw new TokenBadRequestException("유효하지 않은 토큰입니다.");
		}
	}
}
