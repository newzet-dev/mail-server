package com.newzet.api.auth.business.service;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newzet.api.auth.business.dto.JwtResponse;
import com.newzet.api.auth.business.dto.OAuthLoginResponse;
import com.newzet.api.auth.business.dto.OAuthMappingEntityDto;
import com.newzet.api.auth.business.dto.TokenDTO;
import com.newzet.api.auth.business.service.oauth.OAuthRepository;
import com.newzet.api.auth.business.service.oauth.OAuthService;
import com.newzet.api.auth.domain.OAuthMapping;
import com.newzet.api.auth.domain.OAuthProvider;
import com.newzet.api.auth.domain.OAuthToken;
import com.newzet.api.auth.domain.OAuthUserInfo;
import com.newzet.api.auth.domain.Token;
import com.newzet.api.auth.exception.OAuthBadRequestException;
import com.newzet.api.auth.exception.OAuthNotFoundException;
import com.newzet.api.user.business.dto.UserEntityDto;
import com.newzet.api.user.business.service.UserFactory;
import com.newzet.api.user.business.service.UserRepository;
import com.newzet.api.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

	private final Map<String, OAuthService> oAuthServices;
	private final OAuthRepository oAuthRepository;
	private final UserRepository userRepository;
	private final JwtFactory jwtFactory;
	private final TokenRepository tokenRepository;

	@Value("${oauth.web-redirect-uri}")
	private String webRedirectUri;

	@Value("${oauth.app-redirect-uri}")
	private String appRedirectUri;

	public String getOAuthLoginUrl(OAuthProvider provider, String state) {
		OAuthService oAuthService = getOAuthService(provider);

		if (!oAuthService.isBackendRedirect()) {
			throw new OAuthBadRequestException("이 제공자는 백엔드 리다이렉트를 지원하지 않습니다.");
		}

		return oAuthService.getRedirectUrl(state);
	}

	@Transactional
	public URI handleOAuthCallback(OAuthProvider provider, String code, String state) {
		String deviceType = extractDeviceTypeFromState(state);

		OAuthLoginResponse loginResponse = processOAuthLogin(provider, code, deviceType);

		String baseRedirectUrl = getRedirectUriByDeviceType(deviceType);

		StringBuilder redirectBuilder = new StringBuilder(baseRedirectUrl);

		String urlFragmentDelimiter = "#";

		redirectBuilder.append(urlFragmentDelimiter)
			.append("needRegister=").append(loginResponse.needRegister());

		if (loginResponse.needRegister()) {
			redirectBuilder.append("&provider=").append(provider.name().toLowerCase())
				.append("&deviceType=").append(deviceType)
				.append("&oAuthMappingEntityId=")
				.append(loginResponse.OAuthMappingEntityId());
		} else {
			redirectBuilder.append("&accessToken=").append(loginResponse.accessToken())
				.append("&refreshToken=").append(loginResponse.refreshToken());
		}

		return URI.create(redirectBuilder.toString());
	}

	private String getRedirectUriByDeviceType(String deviceType) {
		if (isAppDevice(deviceType)) {
			return appRedirectUri;
		}
		return webRedirectUri;
	}

	private boolean isAppDevice(String deviceType) {
		return "mobile".equalsIgnoreCase(deviceType) ||
			"app".equalsIgnoreCase(deviceType);
	}

	private String extractDeviceTypeFromState(String state) {
		String deviceType = "web";
		if (state != null && !state.isEmpty()) {
			deviceType = state;
		}
		return deviceType;
	}

	@Transactional
	public OAuthLoginResponse processOAuthLogin(OAuthProvider provider, String code,
		String deviceType) {
		OAuthService oAuthService = getOAuthService(provider);

		OAuthUserInfo userInfo = oAuthService.getUserInfo(code);

		OAuthToken oauthToken = userInfo.getOAuthToken();

		Optional<OAuthMappingEntityDto> existingMappingDto =
			oAuthRepository.findBySocialUserIdAndProvider(userInfo.getSocialUserId(), provider);

		if (existingMappingDto.isPresent()) {
			OAuthMappingEntityDto mappingDto = existingMappingDto.get();

			OAuthMapping oAuthMappingDomain = OAuthMapping.fromDto(mappingDto);

			OAuthMapping updatedDomain = oAuthMappingDomain.updateToken(oauthToken);

			OAuthMappingEntityDto updatedDto = updatedDomain.toDto();
			oAuthRepository.update(updatedDto);

			if (oAuthMappingDomain.getUserId() != null && !oAuthMappingDomain.isTemporary()) {
				JwtResponse jwtResponse = generateTokensForUser(oAuthMappingDomain.getUserId(),
					deviceType);
				return OAuthLoginResponse.toJwt(jwtResponse.accessToken(),
					jwtResponse.refreshToken());
			} else {
				return OAuthLoginResponse.toSignUp(updatedDto.getId());
			}
		}

		OAuthMapping tempMapping = OAuthMapping.createTemporary(
			userInfo.getSocialUserId(),
			userInfo.getEmail(),
			userInfo.getName(),
			provider,
			oauthToken
		);

		OAuthMappingEntityDto newMapping = tempMapping.toDto();
		OAuthMappingEntityDto savedMapping = oAuthRepository.save(newMapping);

		return OAuthLoginResponse.toSignUp(savedMapping.getId());
	}

	@Transactional
	public JwtResponse linkOAuthWithUser(User user, String oauthMappingEntityId,
		OAuthProvider provider, String deviceType) {
		Optional<OAuthMappingEntityDto> oAuthMappingEntityDtoOpt =
			oAuthRepository.findByOauthMappingEntityIdAndProvider(
				UUID.fromString(oauthMappingEntityId), provider);

		if (oAuthMappingEntityDtoOpt.isEmpty()) {
			throw new OAuthNotFoundException("OAuth 정보를 찾을 수 없습니다.");
		}

		OAuthMappingEntityDto mappingDto = oAuthMappingEntityDtoOpt.get();

		OAuthMapping oAuthMappingDomain = OAuthMapping.fromDto(mappingDto);

		OAuthMapping updatedDomain = oAuthMappingDomain.linkToUser(user.getId());

		OAuthMappingEntityDto updatedDto = updatedDomain.toDto();
		oAuthRepository.update(updatedDto);

		return generateTokensForUser(user.getId(), deviceType);
	}

	private JwtResponse generateTokensForUser(UUID userId, String deviceType) {
		UserEntityDto userEntityDto = userRepository.getById(userId);
		UserFactory.create(userEntityDto.getId(), userEntityDto.getEmail(),
			userEntityDto.getNickname(), userEntityDto.getStatus());

		Token accessToken = jwtFactory.createAccessToken(userId);
		Token refreshToken = jwtFactory.createRefreshToken(userId);

		TokenDTO refreshTokenDTO = refreshToken.toTokenDTO();

		tokenRepository.saveToken(userId, deviceType, refreshTokenDTO);

		return new JwtResponse(accessToken.getValue(), refreshToken.getValue());
	}

	private OAuthService getOAuthService(OAuthProvider provider) {
		String serviceBeanName = provider.name().toLowerCase() + "OAuthService";
		OAuthService service = oAuthServices.get(serviceBeanName);

		if (service == null) {
			throw new OAuthBadRequestException("지원하지 않는 OAuth 제공자입니다: " + provider);
		}

		return service;
	}
}
