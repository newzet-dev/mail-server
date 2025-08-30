package com.newzet.api.fcm.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.newzet.api.common.auth.annotation.Login;
import com.newzet.api.common.auth.annotation.RequireAuth;
import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.response.SuccessResponse;
import com.newzet.api.fcm.api.dto.FcmTokenDeleteRequest;
import com.newzet.api.fcm.api.dto.FcmTokenUpsertRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/api/v1/fcm")
@RequireAuth
@Tag(name = "FCM 토큰 관리", description = "FCM 토큰 관련 API")
public interface FcmTokenApi {

	@PostMapping("/token")
	@Operation(summary = "fcm 토큰 갱신 or 추가",
		description = "신규 FCM 토큰을 저장한다.")
	SuccessResponse<Object> createFcmToken(@Login AuthUser authUser,
		@Valid @RequestBody FcmTokenUpsertRequest request);

	@DeleteMapping("/token")
	@Operation(summary = "fcm 토큰 삭제",
		description = "신규 FCM 토큰을 삭제한다.")
	SuccessResponse<Object> deleteFcmToken(
		@Login AuthUser authUser,
		@Valid @RequestBody FcmTokenDeleteRequest request);
}
