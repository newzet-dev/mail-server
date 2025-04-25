package com.newzet.api.auth.repository.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.newzet.api.auth.business.dto.OAuthMappingEntityDto;
import com.newzet.api.auth.domain.OAuthProvider;
import com.newzet.api.auth.domain.OAuthToken;

class OAuthMappingEntityTest {

	@Test
	void fromEntityDto_WithToken() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		OAuthToken token = OAuthToken.builder()
			.provider(OAuthProvider.KAKAO)
			.accessToken("access-token")
			.refreshToken("refresh-token")
			.tokenPrefix("bearer")
			.expiresIn(3600L)
			.scope("profile")
			.issuedAt(LocalDateTime.now())
			.build();

		OAuthMappingEntityDto dto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			userId,
			token,
			false
		);

		// When
		OAuthMappingEntity entity = OAuthMappingEntity.fromEntityDto(dto);

		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getSocialUserId()).isEqualTo("social-123");
		assertThat(entity.getSocialUserEmail()).isEqualTo("user@example.com");
		assertThat(entity.getSocialUserName()).isEqualTo("User Name");
		assertThat(entity.getProvider()).isEqualTo(OAuthProvider.KAKAO);
		assertThat(entity.getUserId()).isEqualTo(userId);
		assertThat(entity.getAccessToken()).isEqualTo("access-token");
		assertThat(entity.getRefreshToken()).isEqualTo("refresh-token");
		assertThat(entity.getTokenPrefix()).isEqualTo("bearer");
		assertThat(entity.getExpiresIn()).isEqualTo(3600L);
		assertThat(entity.getScope()).isEqualTo("profile");
		assertThat(entity.getTokenIssuedAt()).isNotNull();
		assertThat(entity.isTemporary()).isFalse();
	}

	@Test
	void fromEntityDto_WithoutToken() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		OAuthMappingEntityDto dto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			userId,
			null,
			true
		);

		// When
		OAuthMappingEntity entity = OAuthMappingEntity.fromEntityDto(dto);

		// Then
		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isEqualTo(id);
		assertThat(entity.getSocialUserId()).isEqualTo("social-123");
		assertThat(entity.getSocialUserEmail()).isEqualTo("user@example.com");
		assertThat(entity.getSocialUserName()).isEqualTo("User Name");
		assertThat(entity.getProvider()).isEqualTo(OAuthProvider.KAKAO);
		assertThat(entity.getUserId()).isEqualTo(userId);
		assertThat(entity.getAccessToken()).isNull();
		assertThat(entity.getRefreshToken()).isNull();
		assertThat(entity.getTokenPrefix()).isNull();
		assertThat(entity.getExpiresIn()).isNull();
		assertThat(entity.getScope()).isNull();
		assertThat(entity.getTokenIssuedAt()).isNull();
		assertThat(entity.isTemporary()).isTrue();
	}

	@Test
	void toEntityDto_WithToken() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		LocalDateTime issuedAt = LocalDateTime.now();

		OAuthMappingEntity entity = createEntityWithToken(id, userId, issuedAt, false);

		// When
		OAuthMappingEntityDto dto = entity.toEntityDto();

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(id);
		assertThat(dto.getSocialUserId()).isEqualTo("social-123");
		assertThat(dto.getSocialUserEmail()).isEqualTo("user@example.com");
		assertThat(dto.getSocialUserName()).isEqualTo("User Name");
		assertThat(dto.getProvider()).isEqualTo(OAuthProvider.KAKAO);
		assertThat(dto.getUserId()).isEqualTo(userId);
		assertThat(dto.getOauthToken()).isNotNull();
		assertThat(dto.getOauthToken().getAccessToken()).isEqualTo("access-token");
		assertThat(dto.getOauthToken().getRefreshToken()).isEqualTo("refresh-token");
		assertThat(dto.getOauthToken().getTokenPrefix()).isEqualTo("bearer");
		assertThat(dto.getOauthToken().getExpiresIn()).isEqualTo(3600L);
		assertThat(dto.getOauthToken().getScope()).isEqualTo("profile");
		assertThat(dto.getOauthToken().getIssuedAt()).isEqualTo(issuedAt);
		assertThat(dto.isTemporary()).isFalse();
	}

	@Test
	void toEntityDto_WithoutToken() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		OAuthMappingEntity entity = createEntityWithoutToken(id, userId, true);

		// When
		OAuthMappingEntityDto dto = entity.toEntityDto();

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(id);
		assertThat(dto.getSocialUserId()).isEqualTo("social-123");
		assertThat(dto.getSocialUserEmail()).isEqualTo("user@example.com");
		assertThat(dto.getSocialUserName()).isEqualTo("User Name");
		assertThat(dto.getProvider()).isEqualTo(OAuthProvider.KAKAO);
		assertThat(dto.getUserId()).isEqualTo(userId);
		assertThat(dto.getOauthToken()).isNull();
		assertThat(dto.isTemporary()).isTrue();
	}

	@Test
	void linkToUser_ShouldUpdateUserIdAndTemporaryFlag() {
		// Given
		UUID id = UUID.randomUUID();
		UUID oldUserId = UUID.randomUUID();
		UUID newUserId = UUID.randomUUID();

		OAuthMappingEntity entity = createEntityWithToken(id, oldUserId, LocalDateTime.now(), true);

		// When
		entity.linkToUser(newUserId);

		// Then
		assertThat(entity.getUserId()).isEqualTo(newUserId);
		assertThat(entity.isTemporary()).isFalse();
	}

	@Test
	void updateToken_WithToken() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		LocalDateTime oldIssuedAt = LocalDateTime.now().minusDays(1);
		LocalDateTime newIssuedAt = LocalDateTime.now();

		OAuthMappingEntity entity = createEntityWithToken(id, userId, oldIssuedAt, false);

		OAuthToken newToken = OAuthToken.builder()
			.provider(OAuthProvider.KAKAO)
			.accessToken("new-access-token")
			.refreshToken("new-refresh-token")
			.tokenPrefix("new-bearer")
			.expiresIn(7200L)
			.scope("new-profile")
			.issuedAt(newIssuedAt)
			.build();

		// When
		entity.updateToken(newToken);

		// Then
		assertThat(entity.getAccessToken()).isEqualTo("new-access-token");
		assertThat(entity.getRefreshToken()).isEqualTo("new-refresh-token");
		assertThat(entity.getTokenPrefix()).isEqualTo("new-bearer");
		assertThat(entity.getExpiresIn()).isEqualTo(7200L);
		assertThat(entity.getScope()).isEqualTo("new-profile");
		assertThat(entity.getTokenIssuedAt()).isEqualTo(newIssuedAt);
	}

	@Test
	void updateToken_WithNullToken() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		LocalDateTime issuedAt = LocalDateTime.now();

		OAuthMappingEntity entity = createEntityWithToken(id, userId, issuedAt, false);
		String originalAccessToken = entity.getAccessToken();
		String originalRefreshToken = entity.getRefreshToken();

		// When
		entity.updateToken(null);

		// Then - 값이 변하지 않아야 함
		assertThat(entity.getAccessToken()).isEqualTo(originalAccessToken);
		assertThat(entity.getRefreshToken()).isEqualTo(originalRefreshToken);
	}

	@Test
	void buildOAuthToken_WithNullAccessToken_ShouldReturnNull() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		OAuthMappingEntity entity = createEntityWithoutToken(id, userId, false);

		// When
		OAuthMappingEntityDto dto = entity.toEntityDto();

		// Then
		assertThat(dto.getOauthToken()).isNull();
	}

	@Test
	void buildOAuthToken_WithNullTokenIssuedAt_ShouldCreateTokenWithCurrentTime() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		OAuthMappingEntity entity = OAuthMappingEntity.builder()
			.id(id)
			.socialUserId("social-123")
			.socialUserEmail("user@example.com")
			.socialUserName("User Name")
			.provider(OAuthProvider.KAKAO)
			.userId(userId)
			.accessToken("access-token")
			.refreshToken("refresh-token")
			.tokenPrefix("bearer")
			.expiresIn(3600L)
			.scope("profile")
			.tokenIssuedAt(null)
			.temporary(false)
			.build();

		// When
		OAuthMappingEntityDto dto = entity.toEntityDto();

		// Then
		assertThat(dto.getOauthToken()).isNotNull();
		assertThat(dto.getOauthToken().getAccessToken()).isEqualTo("access-token");
		assertThat(dto.getOauthToken().getIssuedAt()).isNotNull();
	}

	private OAuthMappingEntity createEntityWithToken(UUID id, UUID userId, LocalDateTime issuedAt,
		boolean temporary) {
		return OAuthMappingEntity.builder()
			.id(id)
			.socialUserId("social-123")
			.socialUserEmail("user@example.com")
			.socialUserName("User Name")
			.provider(OAuthProvider.KAKAO)
			.userId(userId)
			.accessToken("access-token")
			.refreshToken("refresh-token")
			.tokenPrefix("bearer")
			.expiresIn(3600L)
			.scope("profile")
			.tokenIssuedAt(issuedAt)
			.temporary(temporary)
			.build();
	}

	private OAuthMappingEntity createEntityWithoutToken(UUID id, UUID userId, boolean temporary) {
		return OAuthMappingEntity.builder()
			.id(id)
			.socialUserId("social-123")
			.socialUserEmail("user@example.com")
			.socialUserName("User Name")
			.provider(OAuthProvider.KAKAO)
			.userId(userId)
			.temporary(temporary)
			.build();
	}
}
