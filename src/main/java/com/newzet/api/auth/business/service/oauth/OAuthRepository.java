package com.newzet.api.auth.business.service.oauth;

import java.util.Optional;
import java.util.UUID;

import com.newzet.api.auth.business.dto.OAuthMappingEntityDto;
import com.newzet.api.auth.domain.OAuthProvider;

public interface OAuthRepository {

	OAuthMappingEntityDto save(OAuthMappingEntityDto entityDto);

	Optional<OAuthMappingEntityDto> findBySocialUserIdAndProvider(String socialUserId,
		OAuthProvider provider);

	Optional<OAuthMappingEntityDto> findByOauthMappingEntityIdAndProvider(UUID OauthMappingEntityId,
		OAuthProvider provider);

	void update(OAuthMappingEntityDto entityDto);
}
