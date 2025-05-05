package com.newzet.api.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.newzet.api.auth.business.dto.OAuthMappingEntityDto;
import com.newzet.api.auth.business.dto.OAuthTokenDto;
import com.newzet.api.auth.business.service.oauth.OAuthRepository;
import com.newzet.api.auth.domain.OAuthProvider;
import com.newzet.api.auth.exception.OAuthNotFoundException;
import com.newzet.api.auth.repository.entity.OAuthMappingEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OAuthRepositoryImpl implements OAuthRepository {

	private final OAuthMappingJpaRepository oAuthMappingJpaRepository;

	@Override
	public OAuthMappingEntityDto save(OAuthMappingEntityDto entityDto) {
		OAuthMappingEntity entity = OAuthMappingEntity.fromEntityDto(entityDto);
		OAuthMappingEntity oAuthMappingEntity = oAuthMappingJpaRepository.save(entity);
		return oAuthMappingEntity.toEntityDto();
	}

	@Override
	public Optional<OAuthMappingEntityDto> findBySocialUserIdAndProvider(String socialUserId,
		OAuthProvider provider) {
		return oAuthMappingJpaRepository.findBySocialUserIdAndProvider(socialUserId, provider)
			.map(OAuthMappingEntity::toEntityDto);
	}

	@Override
	public Optional<OAuthMappingEntityDto> findByOauthMappingEntityIdAndProvider(
		UUID OauthMappingEntityId, OAuthProvider provider) {
		return oAuthMappingJpaRepository.findByIdAndProvider(OauthMappingEntityId, provider)
			.map(OAuthMappingEntity::toEntityDto);
	}

	@Override
	public void update(OAuthMappingEntityDto entityDto) {
		OAuthMappingEntity existingEntity = oAuthMappingJpaRepository.findById(entityDto.getId())
			.orElseThrow(() -> new OAuthNotFoundException(
				"업데이트할 OAuth 매핑을 찾을 수 없습니다: " + entityDto.getId()));

		existingEntity.updateFromDto(entityDto);
	}

	@Override
	public void updateToken(UUID mappingId, OAuthTokenDto oauthTokenDto) {
		OAuthMappingEntity existingEntity = oAuthMappingJpaRepository.findById(mappingId)
			.orElseThrow(() -> new OAuthNotFoundException(
				"업데이트할 OAuth 매핑을 찾을 수 없습니다: " + mappingId));

		existingEntity.updateFromTokenDto(oauthTokenDto);
	}
}
