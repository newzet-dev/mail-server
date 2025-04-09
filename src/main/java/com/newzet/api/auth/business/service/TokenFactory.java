package com.newzet.api.auth.business.service;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.newzet.api.auth.domain.Token;
import com.newzet.api.auth.domain.TokenType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class TokenFactory {
	private final SecretKey secretKey;

	private static final long ACCESS_TOKEN_VALIDITY_MILLISECONDS = 30 * 60 * 1000;

	private static final long REFRESH_TOKEN_VALIDITY_MILLISECONDS = 14 * 24 * 60 * 60 * 1000;

	public TokenFactory(@Value("${jwt.secret}") String secret) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public Token createAccessToken(String userId) {
		return createToken(userId, TokenType.ACCESS, ACCESS_TOKEN_VALIDITY_MILLISECONDS);
	}

	public Token createRefreshToken(String userId) {
		return createToken(userId, TokenType.REFRESH, REFRESH_TOKEN_VALIDITY_MILLISECONDS);
	}

	private Token createToken(String userId, TokenType tokenType, long validityInMilliseconds) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		String tokenValue = io.jsonwebtoken.Jwts.builder()
			.subject(userId)
			.issuedAt(now)
			.expiration(validity)
			.claim("type", tokenType.name())
			.signWith(secretKey)
			.compact();

		return Token.of(tokenType, tokenValue, userId, now, validity);
	}

	public Optional<Token> parseToken(String token) {
		try {
			Claims claims = io.jsonwebtoken.Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();

			TokenType tokenType = TokenType.valueOf(claims.get("type", String.class));
			String userId = claims.getSubject();
			Date issuedAt = claims.getIssuedAt();
			Date expiration = claims.getExpiration();

			return Optional.of(Token.of(tokenType, token, userId, issuedAt, expiration));
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
				 SignatureException | IllegalArgumentException e) {
			return Optional.empty();
		}
	}
}
