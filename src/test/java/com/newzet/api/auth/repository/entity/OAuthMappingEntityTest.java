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

		OAuthToken token = OAuthToken.create(
			OAuthProvider.KAKAO, "access-token", "refresh-token", 3600L);

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
		assertThat(entity.getExpiresIn()).isEqualTo(3600L);
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
		assertThat(entity.getExpiresIn()).isNull();
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
		assertThat(dto.getOauthToken().getExpiresIn()).isEqualTo(3600L);
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
			.expiresIn(3600L)
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

	@Test
	void updateFromDto_WhenAllFieldsProvided_ShouldUpdateAllFields() {
		// Given
		UUID id = UUID.randomUUID();
		UUID oldUserId = UUID.randomUUID();
		UUID newUserId = UUID.randomUUID();
		LocalDateTime oldIssuedAt = LocalDateTime.now().minusDays(1);
		LocalDateTime newIssuedAt = LocalDateTime.now();

		OAuthMappingEntity entity = createEntityWithToken(id, oldUserId, oldIssuedAt, true);

		OAuthToken newToken = OAuthToken.create(
			OAuthProvider.KAKAO, "new-access-token", "new-refresh-token", 7200L);

		OAuthMappingEntityDto updateDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"updated@example.com",
			"Updated Name",
			OAuthProvider.KAKAO,
			newUserId,
			newToken,
			false
		);

		// When
		entity.updateFromDto(updateDto);

		// Then
		assertThat(entity.getUserId()).isEqualTo(newUserId);
		assertThat(entity.getSocialUserEmail()).isEqualTo("updated@example.com");
		assertThat(entity.getSocialUserName()).isEqualTo("Updated Name");
		assertThat(entity.getAccessToken()).isEqualTo("new-access-token");
		assertThat(entity.getRefreshToken()).isEqualTo("new-refresh-token");
		assertThat(entity.getExpiresIn()).isEqualTo(7200L);
		assertThat(entity.isTemporary()).isFalse();
	}

	@Test
	void updateFromDto_WhenUserIdIsNull_ShouldNotUpdateUserId() {
		// Given
		UUID id = UUID.randomUUID();
		UUID originalUserId = UUID.randomUUID();
		LocalDateTime issuedAt = LocalDateTime.now();

		OAuthMappingEntity entity = createEntityWithToken(id, originalUserId, issuedAt, true);

		OAuthMappingEntityDto updateDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"updated@example.com",
			"Updated Name",
			OAuthProvider.KAKAO,
			null,
			null,
			false
		);

		// When
		entity.updateFromDto(updateDto);

		// Then
		assertThat(entity.getUserId()).isEqualTo(originalUserId);
		assertThat(entity.getSocialUserEmail()).isEqualTo("updated@example.com");
		assertThat(entity.getSocialUserName()).isEqualTo("Updated Name");
		assertThat(entity.isTemporary()).isFalse();
	}

	@Test
	void updateFromDto_WhenOAuthTokenIsNull_ShouldNotUpdateTokenFields() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		UUID newUserId = UUID.randomUUID();
		LocalDateTime issuedAt = LocalDateTime.now();

		OAuthMappingEntity entity = createEntityWithToken(id, userId, issuedAt, true);
		String originalAccessToken = entity.getAccessToken();
		String originalRefreshToken = entity.getRefreshToken();
		Long originalExpiresIn = entity.getExpiresIn();
		LocalDateTime originalIssuedAt = entity.getTokenIssuedAt();

		OAuthMappingEntityDto updateDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"updated@example.com",
			"Updated Name",
			OAuthProvider.KAKAO,
			newUserId,
			null,
			false
		);

		// When
		entity.updateFromDto(updateDto);

		// Then
		assertThat(entity.getUserId()).isEqualTo(newUserId);
		assertThat(entity.getSocialUserEmail()).isEqualTo("updated@example.com");
		assertThat(entity.getSocialUserName()).isEqualTo("Updated Name");
		assertThat(entity.getAccessToken()).isEqualTo(originalAccessToken);
		assertThat(entity.getRefreshToken()).isEqualTo(originalRefreshToken);
		assertThat(entity.getExpiresIn()).isEqualTo(originalExpiresIn);
		assertThat(entity.getTokenIssuedAt()).isEqualTo(originalIssuedAt);
		assertThat(entity.isTemporary()).isFalse();
	}

	@Test
	void updateFromDto_WhenSocialUserEmailIsNull_ShouldNotUpdateEmail() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		LocalDateTime issuedAt = LocalDateTime.now();

		OAuthMappingEntity entity = createEntityWithToken(id, userId, issuedAt, true);
		String originalEmail = entity.getSocialUserEmail();

		OAuthMappingEntityDto updateDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			null,
			"Updated Name",
			OAuthProvider.KAKAO,
			userId,
			null,
			false
		);

		// When
		entity.updateFromDto(updateDto);

		// Then
		assertThat(entity.getSocialUserEmail()).isEqualTo(originalEmail);
		assertThat(entity.getSocialUserName()).isEqualTo("Updated Name");
		assertThat(entity.isTemporary()).isFalse();
	}

	@Test
	void updateFromDto_WhenSocialUserNameIsNull_ShouldNotUpdateName() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		LocalDateTime issuedAt = LocalDateTime.now();

		OAuthMappingEntity entity = createEntityWithToken(id, userId, issuedAt, true);
		String originalName = entity.getSocialUserName();

		OAuthMappingEntityDto updateDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"updated@example.com",
			null,
			OAuthProvider.KAKAO,
			userId,
			null,
			false
		);

		// When
		entity.updateFromDto(updateDto);

		// Then
		assertThat(entity.getSocialUserEmail()).isEqualTo("updated@example.com");
		assertThat(entity.getSocialUserName()).isEqualTo(originalName);
		assertThat(entity.isTemporary()).isFalse();
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
			.expiresIn(3600L)
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
