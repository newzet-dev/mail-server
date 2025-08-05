package com.newzet.api.welcome.presentation.controller;

import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.response.ResponseCode;
import com.newzet.api.common.response.SuccessResponse;
import com.newzet.api.welcome.api.WelcomeApi;
import com.newzet.api.welcome.orchestrator.WelcomeOrchestrator;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WelcomeController implements WelcomeApi {

	private final WelcomeOrchestrator welcomeOrchestrator;

	@Override
	public SuccessResponse<Object> sendWelcomeMail(AuthUser authUser) {
		welcomeOrchestrator.sendWelcomeMail(authUser.getId());
		return SuccessResponse.create(ResponseCode.SUCCESS, "웰컴메일 전송 성공", null);
	}
}
