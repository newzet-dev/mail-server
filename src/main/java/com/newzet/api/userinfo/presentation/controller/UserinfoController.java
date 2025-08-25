package com.newzet.api.userinfo.presentation.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.common.auth.annotation.Login;
import com.newzet.api.common.auth.annotation.RequireAuth;
import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;
import com.newzet.api.userinfo.orchestrator.UserinfoOrchestrator;
import com.newzet.api.userinfo.presentation.dto.UniqueMailResponse;
import com.newzet.api.userinfo.presentation.dto.UserinfoInitResponse;
import com.newzet.api.userinfo.presentation.dto.UserinfoUpdateRequest;
import com.newzet.api.userinfo.presentation.dto.UserinfoWithCategoryListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "유저정보", description = "유저정보 관련 API")
@RequestMapping("/api/v1/user")
public class UserinfoController {

	private final UserinfoOrchestrator userinfoOrchestrator;

	@GetMapping("/info")
	@RequireAuth
	@Operation(summary = "유저정보 조회",
		description = "유저정보를 조회한다.")
	public SuccessResponse<UserinfoWithCategoryListResponse> getUserinfo(@Login AuthUser authUser) {
		UserinfoWithCategoryListResponse response = userinfoOrchestrator.getUserinfoWithCategoryList(authUser.getId());
		return SuccessResponse.create(ResponseCode.SUCCESS, "유저정보 조회 성공", response);
	}

	@GetMapping("/info/init")
	@RequireAuth
	@Operation(summary = "유저정보 등록 여부 확인",
		description = "유저정보 등록 여부를 확인한다.")
	public SuccessResponse<UserinfoInitResponse> checkUserInitializeCompleted(@Login AuthUser authUser) {
		UserinfoInitResponse response = userinfoOrchestrator.checkUserInitializeCompleted(authUser.getId());
		return SuccessResponse.create(ResponseCode.SUCCESS, "유저정보 등록 여부 조회 성공", response);
	}

	@PatchMapping("/info")
	@RequireAuth
	@Operation(summary = "유저정보 수정",
		description = "유저정보를 수정한다.")
	public SuccessResponse<Object> updateUserinfo(@Valid @RequestBody UserinfoUpdateRequest request, UUID userId) {
		userinfoOrchestrator.updateUserinfo(userId, request.email(), request.nickname(), request.userCategory());
		return SuccessResponse.create(ResponseCode.SUCCESS, "유저정보 수정 성공", null);
	}

	@GetMapping("/info/mail/exist")
	@Operation(summary = "메일 중복조회",
		description = "메일이 사용 가능한지 조회한다. 휴면유저/탈퇴한 유저의 메일도 사용 불가.")
	public SuccessResponse<UniqueMailResponse> checkEmailUniqueness(
		@RequestParam("v") String email) {
		UniqueMailResponse result = userinfoOrchestrator.checkEmailUniqueness(email);
		return SuccessResponse.create(ResponseCode.SUCCESS, "메일 중복조회 성공", result);
	}

	@DeleteMapping("/info")
	@RequireAuth
	@Operation(summary = "유저 정보 삭제",
		description = "유저 정보를 삭제한다.")
	public SuccessResponse<Object> deleteUserinfo(@Login AuthUser authUser) {
		userinfoOrchestrator.deleteUserinfo(authUser.getId());
		return SuccessResponse.create(ResponseCode.SUCCESS, "유저 정보를 삭제한다.", null);
	}
}
