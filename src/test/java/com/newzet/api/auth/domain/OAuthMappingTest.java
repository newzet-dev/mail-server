package com.newzet.api.auth.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.newzet.api.auth.business.dto.OAuthMappingEntityDto;

class OAuthMappingTest {

	@Test
	void fromDto_WithToken() {
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
		OAuthMapping domain = OAuthMapping.fromDto(dto);

		// Then
		assertThat(domain).isNotNull();
		assertThat(domain.getId()).isEqualTo(id);
		assertThat(domain.getSocialUserId()).isEqualTo("social-123");
		assertThat(domain.getSocialUserEmail()).isEqualTo("user@example.com");
		assertThat(domain.getSocialUserName()).isEqualTo("User Name");
		assertThat(domain.getProvider()).isEqualTo(OAuthProvider.KAKAO);
		assertThat(domain.getUserId()).isEqualTo(userId);
		assertThat(domain.getOauthToken()).isNotNull();
		assertThat(domain.getOauthToken().getAccessToken()).isEqualTo("access-token");
		assertThat(domain.isTemporary()).isFalse();
	}

	@Test
	void createTemporary() {
		// Given
		String socialId = "social-123";
		String email = "user@example.com";
		String name = "User Name";
		OAuthProvider provider = OAuthProvider.KAKAO;

		OAuthToken token = OAuthToken.builder()
			.provider(provider)
			.accessToken("access-token")
			.refreshToken("refresh-token")
			.tokenPrefix("bearer")
			.expiresIn(3600L)
			.scope("profile")
			.issuedAt(LocalDateTime.now())
			.build();

		// When
		OAuthMapping domain = OAuthMapping.createTemporary(
			socialId, email, name, provider, token
		);

		// Then
		assertThat(domain).isNotNull();
		assertThat(domain.getId()).isNull();
		assertThat(domain.getSocialUserId()).isEqualTo(socialId);
		assertThat(domain.getSocialUserEmail()).isEqualTo(email);
		assertThat(domain.getSocialUserName()).isEqualTo(name);
		assertThat(domain.getProvider()).isEqualTo(provider);
		assertThat(domain.getUserId()).isNull();
		assertThat(domain.getOauthToken()).isEqualTo(token);
		assertThat(domain.isTemporary()).isTrue();
	}

	@Test
	void linkToUser_ShouldCreateNewInstanceWithUpdatedUserIdAndTemporaryFlag() {
		// Given
		UUID id = UUID.randomUUID();
		UUID newUserId = UUID.randomUUID();

		OAuthToken token = OAuthToken.builder()
			.provider(OAuthProvider.KAKAO)
			.accessToken("access-token")
			.refreshToken("refresh-token")
			.tokenPrefix("bearer")
			.expiresIn(3600L)
			.scope("profile")
			.issuedAt(LocalDateTime.now())
			.build();

		OAuthMapping domain = OAuthMapping.builder()
			.id(id)
			.socialUserId("social-123")
			.socialUserEmail("user@example.com")
			.socialUserName("User Name")
			.provider(OAuthProvider.KAKAO)
			.oauthToken(token)
			.temporary(true)
			.build();

		// When
		OAuthMapping linkedDomain = domain.linkToUser(newUserId);

		// Then
		assertThat(linkedDomain).isNotNull();
		assertThat(linkedDomain).isNotSameAs(domain);
		assertThat(linkedDomain.getId()).isEqualTo(id);
		assertThat(linkedDomain.getUserId()).isEqualTo(newUserId);
		assertThat(linkedDomain.isTemporary()).isFalse();

		assertThat(domain.getUserId()).isNull();
		assertThat(domain.isTemporary()).isTrue();
	}

	@Test
	void updateToken_ShouldCreateNewInstanceWithUpdatedToken() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		LocalDateTime oldIssuedAt = LocalDateTime.now().minusDays(1);
		LocalDateTime newIssuedAt = LocalDateTime.now();

		OAuthToken oldToken = OAuthToken.builder()
			.provider(OAuthProvider.KAKAO)
			.accessToken("old-access-token")
			.refreshToken("old-refresh-token")
			.tokenPrefix("old-bearer")
			.expiresIn(3600L)
			.scope("old-profile")
			.issuedAt(oldIssuedAt)
			.build();

		OAuthMapping domain = OAuthMapping.builder()
			.id(id)
			.socialUserId("social-123")
			.socialUserEmail("user@example.com")
			.socialUserName("User Name")
			.provider(OAuthProvider.KAKAO)
			.userId(userId)
			.oauthToken(oldToken)
			.temporary(false)
			.build();

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
		OAuthMapping updatedDomain = domain.updateToken(newToken);

		// Then
		assertThat(updatedDomain).isNotNull();
		assertThat(updatedDomain).isNotSameAs(domain);
		assertThat(updatedDomain.getId()).isEqualTo(id);
		assertThat(updatedDomain.getOauthToken()).isEqualTo(newToken);

		assertThat(domain.getOauthToken()).isEqualTo(oldToken);
	}

	@Test
	void updateToken_WithNullToken_ShouldReturnSameInstance() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		LocalDateTime issuedAt = LocalDateTime.now();

		OAuthToken token = OAuthToken.builder()
			.provider(OAuthProvider.KAKAO)
			.accessToken("access-token")
			.refreshToken("refresh-token")
			.tokenPrefix("bearer")
			.expiresIn(3600L)
			.scope("profile")
			.issuedAt(issuedAt)
			.build();

		OAuthMapping domain = OAuthMapping.builder()
			.id(id)
			.socialUserId("social-123")
			.socialUserEmail("user@example.com")
			.socialUserName("User Name")
			.provider(OAuthProvider.KAKAO)
			.userId(userId)
			.oauthToken(token)
			.temporary(false)
			.build();

		// When
		OAuthMapping updatedDomain = domain.updateToken(null);

		// Then
		assertThat(updatedDomain).isSameAs(domain);
	}

	@Test
	void toDto_ShouldMapCorrectly() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		LocalDateTime issuedAt = LocalDateTime.now();

		OAuthToken token = OAuthToken.builder()
			.provider(OAuthProvider.KAKAO)
			.accessToken("access-token")
			.refreshToken("refresh-token")
			.tokenPrefix("bearer")
			.expiresIn(3600L)
			.scope("profile")
			.issuedAt(issuedAt)
			.build();

		OAuthMapping domain = OAuthMapping.builder()
			.id(id)
			.socialUserId("social-123")
			.socialUserEmail("user@example.com")
			.socialUserName("User Name")
			.provider(OAuthProvider.KAKAO)
			.userId(userId)
			.oauthToken(token)
			.temporary(false)
			.build();

		// When
		OAuthMappingEntityDto dto = domain.toDto();

		// Then
		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo(id);
		assertThat(dto.getSocialUserId()).isEqualTo("social-123");
		assertThat(dto.getSocialUserEmail()).isEqualTo("user@example.com");
		assertThat(dto.getSocialUserName()).isEqualTo("User Name");
		assertThat(dto.getProvider()).isEqualTo(OAuthProvider.KAKAO);
		assertThat(dto.getUserId()).isEqualTo(userId);
		assertThat(dto.getOauthToken()).isEqualTo(token);
		assertThat(dto.isTemporary()).isFalse();
	}
}
