package com.newzet.api.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.user.orchestrator.UserOrchestrator;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "유저", description = "유저 관련 API")
@RequestMapping("/api")
public class UserController {

	private final UserOrchestrator userOrchestrator;

	//TODO: 회원 탈퇴
	//TODO: 유저 정보 등록 여부 확인
}
