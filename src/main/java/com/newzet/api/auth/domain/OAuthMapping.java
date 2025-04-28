package com.newzet.api.auth.domain;

import java.util.UUID;

import com.newzet.api.auth.business.dto.OAuthMappingEntityDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuthMapping {
	private final UUID id;
	private final String socialUserId;
	private final String socialUserEmail;
	private final String socialUserName;
	private final OAuthProvider provider;
	private UUID userId;
	private OAuthToken oauthToken;
	private boolean temporary;

	public static OAuthMapping fromDto(OAuthMappingEntityDto dto) {
		return OAuthMapping.builder()
			.id(dto.getId())
			.socialUserId(dto.getSocialUserId())
			.socialUserEmail(dto.getSocialUserEmail())
			.socialUserName(dto.getSocialUserName())
			.provider(dto.getProvider())
			.userId(dto.getUserId())
			.oauthToken(dto.getOauthToken())
			.temporary(dto.isTemporary())
			.build();
	}

	public static OAuthMapping createTemporary(String providerId, String providerEmail,
		String providerName, OAuthProvider provider, OAuthToken oauthToken) {
		return OAuthMapping.builder()
			.socialUserId(providerId)
			.socialUserEmail(providerEmail)
			.socialUserName(providerName)
			.provider(provider)
			.oauthToken(oauthToken)
			.temporary(true)
			.build();
	}

	public OAuthMapping linkToUser(UUID userId) {
		OAuthMapping linked = OAuthMapping.builder()
			.id(id)
			.socialUserId(socialUserId)
			.socialUserEmail(socialUserEmail)
			.socialUserName(socialUserName)
			.provider(provider)
			.userId(userId)
			.oauthToken(oauthToken)
			.temporary(false)
			.build();

		return linked;
	}

	public OAuthMapping updateToken(OAuthToken newOauthToken) {
		if (newOauthToken == null) {
			return this;
		}

		return OAuthMapping.builder()
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

	public OAuthMappingEntityDto toDto() {
		return OAuthMappingEntityDto.create(
			id,
			socialUserId,
			socialUserEmail,
			socialUserName,
			provider,
			userId,
			oauthToken,
			temporary
		);
	}
}
