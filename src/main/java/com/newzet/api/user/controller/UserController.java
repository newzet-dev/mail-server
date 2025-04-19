package com.newzet.api.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newzet.api.auth.business.dto.JwtResponse;
import com.newzet.api.auth.domain.AuthUser;
import com.newzet.api.auth.infrastructure.annotation.Login;
import com.newzet.api.auth.infrastructure.annotation.RequireAuth;
import com.newzet.api.user.business.dto.SignupRequest;
import com.newzet.api.user.business.dto.UniqueMailResponse;
import com.newzet.api.user.business.dto.UpdateNicknameRequest;
import com.newzet.api.user.business.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "유저", description = "유저 관련 API")
@RequestMapping("/api")
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	@Operation(summary = "회원 가입",
		description = "Oauth 로직 통과 후 매칭되는 유저가 없을 시 회원가입을 진행한다.")
	public ResponseEntity<JwtResponse> signup(@Valid @RequestBody SignupRequest request) {
		JwtResponse jwtResponse = userService.signUp(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(jwtResponse);
	}

	@GetMapping("/my/mail")
	@Operation(summary = "메일 중복조회",
		description = "메일이 사용 가능한지 조회한다. 휴면유저/탈퇴한 유저의 메일도 사용 불가.")
	public ResponseEntity<UniqueMailResponse> checkEmailUniqueness(
		@RequestParam("v") String email) {
		UniqueMailResponse result = userService.checkEmailUniqueness(email);
		return ResponseEntity.ok(result);
	}

	@PutMapping("/my/nickname")
	@RequireAuth
	@Operation(summary = "닉네임 변경", description = "닉네임을 변경한다.")
	public ResponseEntity<Void> updateNickname(
		@Parameter(hidden = true) @Login AuthUser authUser,
		@Valid @RequestBody UpdateNicknameRequest request) {
		userService.updateNickname(authUser.getId(), request);
		return ResponseEntity.ok().build();
	}

	//TODO: 로그아웃
}
