package com.newzet.api.auth.repository.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.newzet.api.auth.business.dto.OAuthMappingEntityDto;
import com.newzet.api.auth.domain.OAuthProvider;
import com.newzet.api.auth.domain.OAuthToken;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "OAUTH_MAPPING")
public class OAuthMappingEntity {
	@Id
	@UuidGenerator
	@Column(columnDefinition = "UUID", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false)
	private String socialUserId;

	private String socialUserEmail;

	private String socialUserName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OAuthProvider provider;

	private UUID userId;

	private String accessToken;

	private String refreshToken;

	private String tokenPrefix;

	private Long expiresIn;

	private String scope;

	private LocalDateTime tokenIssuedAt;

	@Column(nullable = false)
	private boolean temporary;

	public static OAuthMappingEntity fromEntityDto(OAuthMappingEntityDto dto) {
		OAuthMappingEntity entity = OAuthMappingEntity.builder()
			.id(dto.getId())
			.socialUserId(dto.getSocialUserId())
			.socialUserEmail(dto.getSocialUserEmail())
			.socialUserName(dto.getSocialUserName())
			.provider(dto.getProvider())
			.userId(dto.getUserId())
			.temporary(dto.isTemporary())
			.build();

		OAuthToken oauthToken = dto.getOauthToken();
		if (oauthToken != null) {
			entity.accessToken = oauthToken.getAccessToken();
			entity.refreshToken = oauthToken.getRefreshToken();
			entity.tokenPrefix = oauthToken.getTokenPrefix();
			entity.expiresIn = oauthToken.getExpiresIn();
			entity.scope = oauthToken.getScope();
			entity.tokenIssuedAt = oauthToken.getIssuedAt();
		}

		return entity;
	}

	public void linkToUser(UUID userId) {
		this.userId = userId;
		this.temporary = false;
	}

	public void updateToken(OAuthToken oauthToken) {
		if (oauthToken != null) {
			this.accessToken = oauthToken.getAccessToken();
			this.refreshToken = oauthToken.getRefreshToken();
			this.tokenPrefix = oauthToken.getTokenPrefix();
			this.expiresIn = oauthToken.getExpiresIn();
			this.scope = oauthToken.getScope();
			this.tokenIssuedAt = oauthToken.getIssuedAt();
		}
	}

	public OAuthMappingEntityDto toEntityDto() {
		OAuthToken oauthToken = buildOAuthToken();

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

	private OAuthToken buildOAuthToken() {
		if (accessToken == null) {
			return null;
		}

		return OAuthToken.builder()
			.provider(provider)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.tokenPrefix(tokenPrefix)
			.expiresIn(expiresIn)
			.scope(scope)
			.issuedAt(tokenIssuedAt != null ? tokenIssuedAt : LocalDateTime.now())
			.build();
	}
}
