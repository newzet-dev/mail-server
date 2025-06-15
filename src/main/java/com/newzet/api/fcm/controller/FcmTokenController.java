package com.newzet.api.fcm.controller;

import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;
import com.newzet.api.fcm.api.FcmTokenApi;
import com.newzet.api.fcm.api.dto.FcmTokenDeleteRequest;
import com.newzet.api.fcm.api.dto.FcmTokenUpsertRequest;
import com.newzet.api.fcm.orchestrator.FcmTokenOrchestrator;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FcmTokenController implements FcmTokenApi {

	private final FcmTokenOrchestrator fcmTokenOrchestrator;

	public SuccessResponse<Object> createFcmToken(AuthUser authUser, FcmTokenUpsertRequest request) {
		fcmTokenOrchestrator.upsertFcmToken(authUser.getId(), request.fcmToken());
		return SuccessResponse.create(ResponseCode.SUCCESS, "fcm 토큰 저장 성공", null);
	}

	public SuccessResponse<Object> deleteFcmToken(AuthUser authUser, FcmTokenDeleteRequest request) {
		fcmTokenOrchestrator.deleteFcmToken(authUser.getId(), request.fcmToken());
		return SuccessResponse.create(
			ResponseCode.SUCCESS, "fcm 토큰 삭제 성공", null);
	}

}
