package com.newzet.api.auth.business.service.oauth;

import com.newzet.api.auth.domain.OAuthUserInfo;

public interface OAuthService {
	OAuthUserInfo getUserInfo(String code);

	String getRedirectUrl(String state);

	boolean isBackendRedirect();
}
