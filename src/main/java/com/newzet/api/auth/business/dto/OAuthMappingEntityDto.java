package com.newzet.api.auth.business.dto;

import java.util.UUID;

import com.newzet.api.auth.domain.OAuthProvider;
import com.newzet.api.auth.domain.OAuthToken;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
public class OAuthMappingEntityDto {
	private final UUID id;
	private final String socialUserId;
	private final String socialUserEmail;
	private final String socialUserName;
	private final OAuthProvider provider;
	private final UUID userId;
	private final OAuthToken oauthToken;
	private final boolean temporary;

	public static OAuthMappingEntityDto createTemporary(String providerId, String providerEmail,
		String providerName, OAuthProvider provider,
		OAuthToken oauthToken) {
		return OAuthMappingEntityDto.builder()
			.socialUserId(providerId)
			.socialUserEmail(providerEmail)
			.socialUserName(providerName)
			.provider(provider)
			.oauthToken(oauthToken)
			.temporary(true)
			.build();
	}

	public static OAuthMappingEntityDto create(UUID id, String providerId, String providerEmail,
		String providerName, OAuthProvider provider,
		UUID userId, OAuthToken oauthToken, boolean temporary) {
		return OAuthMappingEntityDto.builder()
			.id(id)
			.socialUserId(providerId)
			.socialUserEmail(providerEmail)
			.socialUserName(providerName)
			.provider(provider)
			.userId(userId)
			.oauthToken(oauthToken)
			.temporary(temporary)
			.build();
	}

	public OAuthMappingEntityDto withUserId(UUID userId) {
		return OAuthMappingEntityDto.builder()
			.id(id)
			.socialUserId(socialUserId)
			.socialUserEmail(socialUserEmail)
			.socialUserName(socialUserName)
			.provider(provider)
			.userId(userId)
			.oauthToken(oauthToken)
			.temporary(false)
			.build();
	}

	public OAuthMappingEntityDto withOAuthToken(OAuthToken newOauthToken) {
		return OAuthMappingEntityDto.builder()
			.id(id)
			.socialUserId(socialUserId)
			.socialUserEmail(socialUserEmail)
			.socialUserName(socialUserName)
			.provider(provider)
			.userId(userId)
			.oauthToken(newOauthToken)
			.temporary(temporary)
			.build();
	}
}
