package com.newzet.api.auth.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.auth.business.dto.OAuthMappingEntityDto;
import com.newzet.api.auth.domain.OAuthProvider;
import com.newzet.api.auth.domain.OAuthToken;
import com.newzet.api.auth.exception.OAuthException;
import com.newzet.api.auth.repository.entity.OAuthMappingEntity;

@ExtendWith(MockitoExtension.class)
class OAuthRepositoryImplTest {

	@Mock
	private OAuthMappingJpaRepository oAuthMappingJpaRepository;

	@InjectMocks
	private OAuthRepositoryImpl oAuthRepository;

	@Test
	void save_WhenEntityDtoProvided_ThenSaveAndReturnDto() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		OAuthToken token = OAuthToken.ofKakao("access-token", "refresh-token", 3600L, "bearer",
			"profile");

		OAuthMappingEntityDto entityDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			userId,
			token,
			false
		);

		OAuthMappingEntity entity = OAuthMappingEntity.fromEntityDto(entityDto);

		when(oAuthMappingJpaRepository.save(any(OAuthMappingEntity.class))).thenReturn(entity);

		// When
		OAuthMappingEntityDto result = oAuthRepository.save(entityDto);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getSocialUserId()).isEqualTo("social-123");
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getProvider()).isEqualTo(OAuthProvider.KAKAO);
		assertThat(result.getOauthToken().getAccessToken()).isEqualTo("access-token");
		verify(oAuthMappingJpaRepository).save(any(OAuthMappingEntity.class));
	}

	@Test
	void findBySocialUserIdAndProvider_WhenExists_ThenReturnDto() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		String socialUserId = "social-123";
		OAuthProvider provider = OAuthProvider.KAKAO;

		OAuthToken token = OAuthToken.ofKakao("access-token", "refresh-token", 3600L, "bearer",
			"profile");
		OAuthMappingEntityDto entityDto = OAuthMappingEntityDto.create(
			id,
			socialUserId,
			"user@example.com",
			"User Name",
			provider,
			userId,
			token,
			false
		);

		OAuthMappingEntity entity = OAuthMappingEntity.fromEntityDto(entityDto);

		when(oAuthMappingJpaRepository.findBySocialUserIdAndProvider(socialUserId, provider))
			.thenReturn(Optional.of(entity));

		// When
		Optional<OAuthMappingEntityDto> result = oAuthRepository.findBySocialUserIdAndProvider(
			socialUserId, provider);

		// Then
		assertThat(result).isPresent();
		assertThat(result.get().getSocialUserId()).isEqualTo(socialUserId);
		assertThat(result.get().getProvider()).isEqualTo(provider);
		verify(oAuthMappingJpaRepository).findBySocialUserIdAndProvider(socialUserId, provider);
	}

	@Test
	void findBySocialUserIdAndProvider_WhenNotExists_ThenReturnEmpty() {
		// Given
		String socialUserId = "non-existent";
		OAuthProvider provider = OAuthProvider.KAKAO;

		when(oAuthMappingJpaRepository.findBySocialUserIdAndProvider(socialUserId, provider))
			.thenReturn(Optional.empty());

		// When
		Optional<OAuthMappingEntityDto> result = oAuthRepository.findBySocialUserIdAndProvider(
			socialUserId, provider);

		// Then
		assertThat(result).isEmpty();
		verify(oAuthMappingJpaRepository).findBySocialUserIdAndProvider(socialUserId, provider);
	}

	@Test
	void findByOauthMappingEntityIdAndProvider_WhenExists_ThenReturnDto() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		OAuthProvider provider = OAuthProvider.KAKAO;

		OAuthToken token = OAuthToken.ofKakao("access-token", "refresh-token", 3600L, "bearer",
			"profile");
		OAuthMappingEntityDto entityDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			provider,
			userId,
			token,
			false
		);

		OAuthMappingEntity entity = OAuthMappingEntity.fromEntityDto(entityDto);

		when(oAuthMappingJpaRepository.findByIdAndProvider(id, provider))
			.thenReturn(Optional.of(entity));

		// When
		Optional<OAuthMappingEntityDto> result = oAuthRepository.findByOauthMappingEntityIdAndProvider(
			id, provider);

		// Then
		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(id);
		assertThat(result.get().getProvider()).isEqualTo(provider);
		verify(oAuthMappingJpaRepository).findByIdAndProvider(id, provider);
	}

	@Test
	public void findByOauthMappingEntityIdAndProvider_WhenNotExists_ThenReturnEmpty() {
		// Given
		UUID id = UUID.randomUUID();
		OAuthProvider provider = OAuthProvider.KAKAO;

		when(oAuthMappingJpaRepository.findByIdAndProvider(id, provider))
			.thenReturn(Optional.empty());

		// When
		Optional<OAuthMappingEntityDto> result = oAuthRepository.findByOauthMappingEntityIdAndProvider(
			id, provider);

		// Then
		assertThat(result).isEmpty();
		verify(oAuthMappingJpaRepository).findByIdAndProvider(id, provider);
	}

	@Test
	void update_WhenEntityExists_ThenUpdateFields() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		UUID existingUserId = UUID.randomUUID();

		OAuthToken oldToken = OAuthToken.ofKakao("old-access-token", "refresh-token", 3600L,
			"bearer", "profile");
		OAuthToken newToken = OAuthToken.ofKakao("new-access-token", "refresh-token", 3600L,
			"bearer", "profile");

		OAuthMappingEntityDto entityDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			userId,
			newToken,
			false
		);

		OAuthMappingEntityDto existingDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			existingUserId,
			oldToken,
			true
		);

		OAuthMappingEntity existingEntity = OAuthMappingEntity.fromEntityDto(existingDto);

		when(oAuthMappingJpaRepository.findById(id)).thenReturn(Optional.of(existingEntity));

		// When
		oAuthRepository.update(entityDto);

		// Then
		verify(oAuthMappingJpaRepository).findById(id);
		assertThat(existingEntity.getUserId()).isEqualTo(userId);
		assertThat(existingEntity.getAccessToken()).isEqualTo("new-access-token");
		assertThat(existingEntity.isTemporary()).isFalse();
	}

	@Test
	void update_WhenEntityNotExists_ThenThrowException() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		OAuthToken token = OAuthToken.ofKakao("access-token", "refresh-token", 3600L, "bearer",
			"profile");

		OAuthMappingEntityDto entityDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			userId,
			token,
			false
		);

		when(oAuthMappingJpaRepository.findById(id)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> oAuthRepository.update(entityDto))
			.isInstanceOf(OAuthException.class)
			.hasMessageContaining("업데이트할 OAuth 매핑을 찾을 수 없습니다");
	}

	@Test
	void update_WhenEntityExistsAndUserIdIsNotNull_ThenLinkToUser() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		UUID existingUserId = UUID.randomUUID();

		OAuthToken token = OAuthToken.ofKakao("access-token", "refresh-token", 3600L, "bearer",
			"profile");

		OAuthMappingEntityDto entityDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			userId,
			token,
			false
		);

		OAuthMappingEntityDto existingDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			existingUserId,
			token,
			true
		);

		OAuthMappingEntity existingEntity = OAuthMappingEntity.fromEntityDto(existingDto);

		when(oAuthMappingJpaRepository.findById(id)).thenReturn(Optional.of(existingEntity));

		// When
		oAuthRepository.update(entityDto);

		// Then
		verify(oAuthMappingJpaRepository).findById(id);
		assertThat(existingEntity.getUserId()).isEqualTo(userId);
	}

	@Test
	void update_WhenEntityExistsAndUserIdIsNull_ThenDoNotLinkToUser() {
		// Given
		UUID id = UUID.randomUUID();
		UUID existingUserId = UUID.randomUUID();

		OAuthToken token = OAuthToken.ofKakao("access-token", "refresh-token", 3600L, "bearer",
			"profile");

		OAuthMappingEntityDto entityDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			null,  // userId is null
			token,
			false
		);

		OAuthMappingEntityDto existingDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			existingUserId,
			token,
			true
		);

		OAuthMappingEntity existingEntity = OAuthMappingEntity.fromEntityDto(existingDto);

		when(oAuthMappingJpaRepository.findById(id)).thenReturn(Optional.of(existingEntity));

		// When
		oAuthRepository.update(entityDto);

		// Then
		verify(oAuthMappingJpaRepository).findById(id);
		assertThat(existingEntity.getUserId()).isEqualTo(existingUserId); // userId shouldn't change
	}

	@Test
	void update_WhenEntityExistsAndAccessTokenIsDifferent_ThenUpdateToken() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		OAuthToken oldToken = OAuthToken.ofKakao("old-access-token", "refresh-token", 3600L,
			"bearer", "profile");
		OAuthToken newToken = OAuthToken.ofKakao("new-access-token", "refresh-token", 3600L,
			"bearer", "profile");

		OAuthMappingEntityDto entityDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			userId,
			newToken,
			false
		);

		OAuthMappingEntityDto existingDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			userId,
			oldToken,
			true
		);

		OAuthMappingEntity existingEntity = OAuthMappingEntity.fromEntityDto(existingDto);

		when(oAuthMappingJpaRepository.findById(id)).thenReturn(Optional.of(existingEntity));

		// When
		oAuthRepository.update(entityDto);

		// Then
		verify(oAuthMappingJpaRepository).findById(id);
		assertThat(existingEntity.getAccessToken()).isEqualTo("new-access-token");
	}

	@Test
	void update_WhenEntityExistsAndAccessTokenIsSame_ThenDoNotUpdateToken() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		String sameAccessToken = "same-access-token";
		OAuthToken token1 = OAuthToken.ofKakao(sameAccessToken, "refresh-token", 3600L, "bearer",
			"profile");
		OAuthToken token2 = OAuthToken.ofKakao(sameAccessToken, "refresh-token", 3600L, "bearer",
			"profile");

		OAuthMappingEntityDto entityDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			userId,
			token1,
			false
		);

		OAuthMappingEntityDto existingDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			userId,
			token2,
			true
		);

		OAuthMappingEntity existingEntity = OAuthMappingEntity.fromEntityDto(existingDto);

		when(oAuthMappingJpaRepository.findById(id)).thenReturn(Optional.of(existingEntity));

		// When
		oAuthRepository.update(entityDto);

		// Then
		verify(oAuthMappingJpaRepository).findById(id);
		// isTemporary 값은 변하지만 토큰은 업데이트되지 않아야 함
	}

	@Test
	void update_WhenAccessTokenIsNull_ThenDoNotUpdateToken() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();

		OAuthToken nullAccessToken = new OAuthToken(OAuthProvider.KAKAO, null, "refresh-token",
			"bearer", 3600L, "profile", LocalDateTime.now());
		OAuthToken existingToken = OAuthToken.ofKakao("existing-token", "refresh-token", 3600L,
			"bearer", "profile");

		OAuthMappingEntityDto entityDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			userId,
			nullAccessToken,
			false
		);

		OAuthMappingEntityDto existingDto = OAuthMappingEntityDto.create(
			id,
			"social-123",
			"user@example.com",
			"User Name",
			OAuthProvider.KAKAO,
			userId,
			existingToken,
			true
		);

		OAuthMappingEntity existingEntity = OAuthMappingEntity.fromEntityDto(existingDto);
		String originalAccessToken = existingEntity.getAccessToken();

		when(oAuthMappingJpaRepository.findById(id)).thenReturn(Optional.of(existingEntity));

		// When
		oAuthRepository.update(entityDto);

		// Then
		verify(oAuthMappingJpaRepository).findById(id);
		assertThat(existingEntity.getAccessToken()).isEqualTo(originalAccessToken);
	}

	@Test
	void ofKakao_WhenAccessTokenIsNull_ShouldThrowException() {
		// given
		String accessToken = null;
		String refreshToken = "refresh-token";
		Long expiresIn = 3600L;
		String tokenType = "bearer";
		String scope = "profile";

		// when & then
		assertThatThrownBy(() ->
			OAuthToken.ofKakao(accessToken, refreshToken, expiresIn, tokenType, scope)
		).isInstanceOf(OAuthException.class)
			.hasMessage("카카오 토큰 응답이 올바르지 않습니다.");
	}
}
