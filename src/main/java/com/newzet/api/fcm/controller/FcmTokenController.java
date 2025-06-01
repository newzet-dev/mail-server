package com.newzet.api.fcm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.common.auth.annotation.Login;
import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;
import com.newzet.api.fcm.business.service.FcmTokenService;
import com.newzet.api.fcm.controller.dto.FcmTokenCreateRequest;
import com.newzet.api.fcm.controller.dto.FcmTokenDeleteRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "FCM 토큰 관리", description = "FCM 토큰 관련 API")
public class FcmTokenController {

	private final FcmTokenService fcmTokenService;

	@PostMapping("/fcm_token")
	@Operation(summary = "fcm 토큰 추가",
		description = "신규 FCM 토큰을 저장한다.")
	public ResponseEntity<SuccessResponse<Object>> createFcmToken(
		@Login AuthUser authUser,
		@Valid @RequestBody FcmTokenCreateRequest request) {
		fcmTokenService.createFcmToken(authUser.getId(), request.fcmToken());
		SuccessResponse<Object> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "fcm 토큰 저장 성공", null);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/fcm_token")
	@Operation(summary = "fcm 토큰 삭제",
		description = "신규 FCM 토큰을 삭제한다.")
	public ResponseEntity<SuccessResponse<Object>> deleteFcmToken(
		@Login AuthUser authUser,
		@Valid @RequestBody FcmTokenDeleteRequest request) {
		fcmTokenService.deleteFcmToken(authUser.getId(), request.fcmToken());
		SuccessResponse<Object> response = SuccessResponse.create(
			ResponseCode.SUCCESS, "fcm 토큰 삭제 성공", null);
		return ResponseEntity.ok(response);
	}

}
