package com.newzet.api.welcome.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.newzet.api.common.auth.annotation.Login;
import com.newzet.api.common.auth.domain.AuthUser;
import com.newzet.api.common.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("/api/v1")
@Tag(name = "Welcome 처리", description = "Welcome 처리 API")
public interface WelcomeApi {

	@PostMapping("/welcome")
	@Operation(summary = "welcome 메일 전송",
		description = "welcome 메일을 전송한다.")
	SuccessResponse<Object> sendWelcomeMail(@Login AuthUser authUser);
}
