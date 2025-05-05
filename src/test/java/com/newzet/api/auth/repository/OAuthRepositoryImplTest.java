package com.newzet.api.auth.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.newzet.api.auth.business.dto.OAuthMappingEntityDto;
import com.newzet.api.auth.domain.OAuthProvider;
import com.newzet.api.auth.domain.OAuthToken;
import com.newzet.api.auth.exception.OAuthErrorException;
import com.newzet.api.auth.exception.OAuthNotFoundException;
import com.newzet.api.auth.repository.entity.OAuthMappingEntity;

@ExtendWith(MockitoExtension.class)
class OAuthRepositoryImplTest {

	@Mock
	private OAuthMappingJpaRepository oAuthMappingJpaRepository;

	@InjectMocks
	private OAuthRepositoryImpl oAuthRepository;

	@Captor
	private ArgumentCaptor<OAuthMappingEntity> entityCaptor;

	@Test
	void save_WhenEntityDtoProvided_ThenSaveAndReturnDto() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		OAuthToken token = OAuthToken.ofKakao("access-token", "refresh-token", 3600L);

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

		OAuthToken token = OAuthToken.ofKakao("access-token", "refresh-token", 3600L);
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

		OAuthToken token = OAuthToken.ofKakao("access-token", "refresh-token", 3600L);
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
	void update_WhenEntityNotExists_ThenThrowException() {
		// Given
		UUID id = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		OAuthToken token = OAuthToken.ofKakao("access-token", "refresh-token", 3600L);

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
			.isInstanceOf(OAuthNotFoundException.class)
			.hasMessageContaining("업데이트할 OAuth 매핑을 찾을 수 없습니다");
	}

	@Test
	void ofKakao_WhenAccessTokenIsNull_ShouldThrowException() {
		// Given
		String accessToken = null;
		String refreshToken = "refresh-token";
		Long expiresIn = 3600L;
		String tokenType = "bearer";
		String scope = "profile";

		// When & Then
		assertThatThrownBy(() ->
			OAuthToken.ofKakao(accessToken, refreshToken, expiresIn)
		).isInstanceOf(OAuthErrorException.class)
			.hasMessage("응답이 올바르지 않아 accessToken이 전달되지 않았습니다.");
	}
}
