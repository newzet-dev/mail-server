package com.newzet.api.auth.business.dto;

import com.newzet.api.auth.domain.DeviceType;
import com.newzet.api.auth.domain.OAuthProvider;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record OAuthContext(OAuthProvider oAuthProvider, DeviceType deviceType) {
	public static OAuthContext of(OAuthProvider oAuthProvider, DeviceType deviceType) {
		return OAuthContext.builder()
			.oAuthProvider(oAuthProvider)
			.deviceType(deviceType)
			.build();
	}
}
